package com.khantilchoksi.healthcareapp;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.AddAppointmentImagesTask;
import com.khantilchoksi.healthcareapp.ArztAsyncCalls.CancelAppointmentTask;
import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetPatientAppointmentDetailsTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewAppointmentActivityFragment extends Fragment implements GetPatientAppointmentDetailsTask.AsyncResponse{

    private String LOG_TAG = getClass().getSimpleName();

    private View mRootView;

    static final int PHOTO_CAPTURE_REQUEST = 1;  // The request code for taking picture
    static final int PICK_PHOTO_REQUEST = 2;        //The request code for getting image from gallery
    static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 3;  // Runtime permissions

    private Uri outputFileUri;  // For the image selection
    private AttachmentImagesArrayAdapter attachmentImagesArrayAdapter;

    private String filePath;        //file name of the image - for server upload

    private String[] encodedBitMapImage;  //bitmap encoded string image url
    private String[] encodedThumbnailImage;

    private Button mUploadImageButton;
    private GridView attachmentImagesGridView;

    private int countImages = 0;

    private ArrayList<String> mAttachmentImagesPathList;
    private ShowImageArrayAdapter showImageArrayAdapter;

    private GridView gridView;

    private EditText mDoctorNameEditText;
    private EditText mDoctorQualificationsEditText;
    private EditText mDoctorSpecialitesEditText;
    private EditText mClinicNameEditText;
    private EditText mAppointmentDateEditText;
    private EditText mVisitingHoursEditText;
    private EditText mConsultationFeesEditText;
    private EditText mClinicAddressEditText;

    private Button mCancelButton;
    private Button mUploadAttachements;

    final Calendar myCalendar = Calendar.getInstance();

    private String mAppointmentId;
    private ProgressDialog progressDialog;

    public ViewAppointmentActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_view_appointment, container, false);
        mAppointmentId = getActivity().getIntent().getStringExtra("appointmentId");

        mDoctorNameEditText = (EditText) mRootView.findViewById(R.id.doctor_name);
        mDoctorQualificationsEditText = (EditText) mRootView.findViewById(R.id.doctor_qualification);
        mDoctorSpecialitesEditText = (EditText) mRootView.findViewById(R.id.doctor_specialities);

        mClinicNameEditText = (EditText) mRootView.findViewById(R.id.clinic_name);

        mVisitingHoursEditText = (EditText) mRootView.findViewById(R.id.visiting_hours);
        mConsultationFeesEditText = (EditText) mRootView.findViewById(R.id.consultancy_fees);
        mClinicAddressEditText = (EditText) mRootView.findViewById(R.id.clinic_address);



        mAppointmentDateEditText = (EditText) mRootView.findViewById(R.id.appointment_date);

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Appointment Info...");
        progressDialog.show();

        GetPatientAppointmentDetailsTask getDoctorClinicSlotInfoTask =
                new GetPatientAppointmentDetailsTask(mAppointmentId,
                getContext(),getActivity(),this,progressDialog);
        getDoctorClinicSlotInfoTask.execute((Void) null);

        showImageArrayAdapter = new ShowImageArrayAdapter(getActivity(), new ArrayList<String>());
        gridView = (GridView) mRootView.findViewById(R.id.attachmentGridView);
        gridView.setAdapter(showImageArrayAdapter);

        mCancelButton = (Button) mRootView.findViewById(R.id.btn_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonClick();
            }
        });

        mUploadAttachements = (Button) mRootView.findViewById(R.id.btn_upload);
        mUploadAttachements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadAttachmentsClick();
            }
        });

        mUploadImageButton = (Button) mRootView.findViewById(R.id.upload_image_button);

        attachmentImagesArrayAdapter = new AttachmentImagesArrayAdapter(getActivity(), new ArrayList<Bitmap>());

        attachmentImagesGridView = (GridView) mRootView.findViewById(R.id.imagesGridView);
        attachmentImagesGridView.setAdapter(attachmentImagesArrayAdapter);


        final Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        attachmentImagesGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long arg3) {
                vibrator.vibrate(100);
                ImageView selectedImage = (ImageView) view.findViewById(R.id.attachment_image_item);
                selectedImage.setColorFilter(Color.GRAY, PorterDuff.Mode.LIGHTEN);

                ImageButton removeImageButton = (ImageButton) view.findViewById(R.id.deleteImageButton);
                removeImageButton.setVisibility(View.VISIBLE);
                return true;
            }
        });

        attachmentImagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG,"On Item Click");

                ImageButton removeImageButton = (ImageButton) view.findViewById(R.id.deleteImageButton);
                removeImageButton.setVisibility(View.INVISIBLE);

                ImageView selectedImage = (ImageView) view.findViewById(R.id.attachment_image_item);
                selectedImage.clearColorFilter();

            }
        });

        //Upload Image
        mUploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the user has already uploaded 3 images or not
                if(attachmentImagesArrayAdapter.getCount()<3)
                    mayRequestUploadImage();
                else
                    Toast.makeText(getActivity(), "You can upload maximum 3 images!", Toast.LENGTH_LONG).show();
            }
        });


        return mRootView;
    }

    private void setAppointmentDate(String birthdate){
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            myCalendar.setTime(sdf.parse(birthdate));
            updateAppointmentDateTextView();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateAppointmentDateTextView(){
        String myFormat = "EEEE, MMMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mAppointmentDateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    public void cancelButtonClick(){

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cancelling Appointment...");
        progressDialog.show();

        CancelAppointmentTask cancelAppointmentTask =
                new CancelAppointmentTask(
                        mAppointmentId,
                        getActivity().getApplicationContext(),getActivity(),progressDialog);
        cancelAppointmentTask.execute((Void) null);
    }

    public void uploadAttachmentsClick(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading Attachments...");
        progressDialog.show();

        AddAppointmentImagesTask addAppointmentImagesTask =
                new AddAppointmentImagesTask(
                        mAppointmentId, attachmentImagesArrayAdapter,
                        getActivity().getApplicationContext(),getActivity(),progressDialog);
        addAppointmentImagesTask.execute((Void) null);
    }

    private void mayRequestUploadImage(){
        if(Build.VERSION.SDK_INT < 23){
            uploadImageIntent();
        }else{
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //That is Permission is denied
                // Should we show an explanation?
                Log.d("Permission","Permission is not granted for write external Storage");
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // Display a SnackBar with an explanation and a button to trigger the request.
                    Snackbar.make(mRootView, R.string.permission_camera,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    Log.d("Permission", "After denying, Permission is Requested for camera!");
                                }

                            })
                            .show();

                }
                else {
                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

                    Log.d("Permission", "Permission is not granted, should show request rational is false for camera");

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                //Permission is already granted
                uploadImageIntent();
            }
        }


    }

    private void uploadImageIntent(){
        //If permission is granted
        outputFileUri = getOutputMediaFile();

        final List<Intent> cameraIntents = new ArrayList<Intent>();

        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        final PackageManager packageManager = getActivity().getPackageManager();

        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, PHOTO_CAPTURE_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:{
                //Case for read and write app permission
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Permission","Permission is Granted for camera.. Calling Intent!");
                    uploadImageIntent();

                } else {
                    //Permission is denied
                    //User has to enter phone number manually

                    Log.d("Permission", "Permission is not granted in result for camera.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // Display a SnackBar with an explanation and a button to trigger the request.
                        Log.d("Permission", "In the result if");
                        Snackbar.make(mRootView, R.string.permission_camera,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                                    }
                                })
                                .show();

                    }else{
                        //User has checked never show me again
                        Log.d("Permission", "User has checked never show me again.");
                        Snackbar.make(mRootView, R.string.permission_setting,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.settings, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getActivity().getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                    }

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }   //switch ended
    }

    /** Create a File for saving an image */
    private static Uri getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        String externalStorageState = Environment.getExternalStorageState();
        Log.d("Storage State:", externalStorageState);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Arzt");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Arzt", "failed to create directory");
                return null;
            }
        }else{
            Log.d("Storage: ",mediaStorageDir.getPath().toString());
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result Code"," "+resultCode);
        Log.d("Request Code", " " + requestCode);
        //final String action2 = data.getAction();
        //Log.d("Action: ", action2);
        switch (requestCode) {
            case PHOTO_CAPTURE_REQUEST: {
                // Make sure the request was successful

                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImageUri;
                    if(data==null)
                        selectedImageUri = outputFileUri;
                    else
                        selectedImageUri = data.getData();
                    //Log.d("Action: ", "Camera Action selected");

                    //Bitmap bitmap = getThumbnail(data.getData());
                    //previewImage.setImageBitmap(bitmap);

                    //Below line not handles clicked image due to large size
                    //previewImage.setImageURI(data.getData());

                    //21st May

                    //First select image view holder

                    Log.d("Data Received:",selectedImageUri.toString());


                    if (selectedImageUri != null && "content".equals(selectedImageUri.getScheme())) {
                        //Image is chosen from Gallery
                        Log.d("Chosen Image:", " is from Gallery");
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                selectedImageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        filePath = cursor.getString(columnIndex);
                        cursor.close();


//                        bitmap = BitmapFactory.decodeFile(filePath);
//
//                        Cursor cursor = this.getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
//                        cursor.moveToFirst();
//                        filePath = cursor.getString(0);
//                        cursor.close();
                    } else {
                        //Image is chosen from Camera
                        Log.d("Chosen Image:"," is from Camera");
                        filePath = selectedImageUri.getPath();
                        // Get the dimensions of the bitmap
                        //filePath = _uri.getPath();
                    }

                    //Getting the original image as bitmap object
                    BitmapFactory.Options bmOriginalOptions = new BitmapFactory.Options();
                    bmOriginalOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, bmOriginalOptions);
                    bmOriginalOptions.inJustDecodeBounds = false;
                    Bitmap bitmapOriginal = BitmapFactory.decodeFile(filePath, bmOriginalOptions);
                    attachmentImagesArrayAdapter.add(bitmapOriginal);
//                    encodedBitMapImage = getStringImage(bitmapOriginal);

                    // Add to the IssueImage Adapter
//                    tempImageView.setImageBitmap(ThumbImage);
//                    issueImageArrayAdapter.add(ThumbImage);
//                    encodedThumbnailImage = getStringImage(ThumbImage);

                    //Converting bitmap image to encoded string for server
//                    encodedBitMapImage = getStringImage(bitmap);
                    Log.d(LOG_TAG,"Encoded Original Image"+ encodedBitMapImage);
                    Log.d(LOG_TAG,"Encoded Thumbnail Image"+ encodedThumbnailImage);
                    Log.d(LOG_TAG,"File Name"+filePath);

/*                    //Getting the bitmap image to scale and show on android screen
                    ImageView tempImageView = getEmptyImageHolder();
                    int targetW = tempImageView.getWidth();
                    int targetH = tempImageView.getHeight();
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(filePath, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

//                    tempImageView.setLayoutParams(new GridView.LayoutParams(85, 85));
//                    tempImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                    tempImageView.setImageBitmap(bitmap);
                    //End of Getting the bitmap image to scale and show on android screen*/



                    //End of 21st may - completed

                    //For camera
                    // Get the dimensions of the View
                    /*InputStream inputStream = null;
                    try {
                        inputStream = getActivity().getContentResolver().openInputStream( data.getData());

                        int targetW = previewImage.getWidth();
                        int targetH = previewImage.getHeight();

                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;

                        //BitmapFactory.decodeFile(data.getData().getPath(), bmOptions);
                        BitmapFactory.decodeStream(inputStream,null,bmOptions);
                        inputStream.close();
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;

                        Bitmap bitmap = BitmapFactory.decodeFile(data.getData().getPath(), bmOptions);
                        previewImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(
                                data.getData(), filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();


                        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        previewImage.setImageBitmap(yourSelectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


                    //Code for gallery
                    /*Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(
                            selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    previewImage.setImageBitmap(yourSelectedImage);*/


                    //Tried with picasso .. problem is with camera captured image
                    //Picasso.with(getContext()).load(data.getData()).into(previewImage);

                    //Bundle extras = data.getExtras();
                    //Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //previewImage.setImageBitmap(imageBitmap);

                    //Log.d("Action:","Hello");
                    //Bundle extras = data.getExtras();
                    //Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //}else{
                    //For gallery
                    //  Uri selectedImage = data.getData();
                    //previewImage.setImageURI(selectedImage);
                    //}


                    /*final String action = data.getAction();
                    Uri selectedImageUri;
                    if (action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)){
                            selectedImageUri = outputFileUri;
                    }else{
                        selectedImageUri = data.getData();
                    }
                    previewImage.setImageURI(selectedImageUri);*/

                    /*final boolean isCamera;
                    if (data == null) {
                        isCamera = true;
                    } else {
                        final String action = data.getAction();
                        if (action == null) {
                            isCamera = false;
                        } else {
                            isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }

                    Uri selectedImageUri;
                    if (isCamera) {
                        //selectedImageUri = outputFileUri;
                        selectedImageUri = data == null ? null : data.getData();
                    } else {
                        selectedImageUri = data == null ? null : data.getData();

                    }
                    previewImage.setImageURI(selectedImageUri);
                }*/
                    break;
                }

            }
            default:
                Toast.makeText(getActivity(),"No Image Uploaded", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void processDoctorClinicTaskInfoFinish(String doctorName,
                                                  String doctorQualificaitons,
                                                  String doctorSpecialities,
                                                  String clinicName, String appointmentDay,
                                                  String appointmentDate, String visitingHours,
                                                  String consultationFees, String clinicAddress,
                                                  ArrayList<String> attachementImagesPath,
                                                  ProgressDialog progressDialog) {
        mDoctorNameEditText.setText(doctorName);
        mDoctorQualificationsEditText.setText(doctorQualificaitons);
        mDoctorSpecialitesEditText.setText(doctorSpecialities);

        mClinicNameEditText.setText(clinicName);
        mAppointmentDateEditText.setText(appointmentDate);
        mVisitingHoursEditText.setText(visitingHours);
        mConsultationFeesEditText.setText(consultationFees);
        mClinicAddressEditText.setText(clinicAddress);

        mAttachmentImagesPathList = attachementImagesPath;

        if(mAttachmentImagesPathList.isEmpty()){
            Log.d(LOG_TAG,"Images path list is empty.");
            //No photos are there for this ticket
            TextView noPhotosTextView = (TextView) mRootView.findViewById(R.id.noPhotosTextView);
            noPhotosTextView.setVisibility(View.VISIBLE);
        }else{
            Log.d(LOG_TAG,"Images path list is NOT empty.");
            gridView.setVisibility(View.VISIBLE);
        }

        for(int i =0; i<mAttachmentImagesPathList.size() ; i++) {
            Log.d(LOG_TAG,"Image path added: "+mAttachmentImagesPathList.get(i));
            showImageArrayAdapter.add(mAttachmentImagesPathList.get(i));
        }

        progressDialog.dismiss();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent pagerIntent = new Intent(getActivity(), ImageViewPagerActivity.class);
//                    pagerIntent.putExtra("IssueImageAdapter",issueImageArrayAdapter);
                Bundle bundle = new Bundle();
                String[] paths = new String[mAttachmentImagesPathList.size()];
                bundle.putStringArray("imagePaths", mAttachmentImagesPathList.toArray(paths));
                bundle.putInt("size", showImageArrayAdapter.getCount());
                bundle.putInt("currentPosition", position);
                pagerIntent.putExtras(bundle);
                startActivity(pagerIntent);
            }
        });
    }


}
