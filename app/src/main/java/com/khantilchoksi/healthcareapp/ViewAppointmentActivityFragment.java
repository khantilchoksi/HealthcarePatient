package com.khantilchoksi.healthcareapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.CancelAppointmentTask;
import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetPatientAppointmentDetailsTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ViewAppointmentActivityFragment extends Fragment implements GetPatientAppointmentDetailsTask.AsyncResponse{

    private String LOG_TAG = getClass().getSimpleName();

    private View mRootView;

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
