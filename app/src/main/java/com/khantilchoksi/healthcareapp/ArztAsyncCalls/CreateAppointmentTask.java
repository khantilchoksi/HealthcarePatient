package com.khantilchoksi.healthcareapp.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.khantilchoksi.healthcareapp.AttachmentImagesArrayAdapter;
import com.khantilchoksi.healthcareapp.HomeActivity;
import com.khantilchoksi.healthcareapp.R;
import com.khantilchoksi.healthcareapp.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Khantil on 22-03-2017.
 */

public class CreateAppointmentTask extends AsyncTask<Void, Void, Boolean> {

    private static final String LOG_TAG = CreateAppointmentTask.class.getSimpleName();
    Context context;
    Activity activity;
    String mDcId;
    String mAppiontmentDate;
    AttachmentImagesArrayAdapter mAttachmentImagesArrayAdapter;
    ProgressDialog progressDialog;
    String issue;

    /*public interface AsyncResponse {
        void processSlotFinish(String response);
    }

    public AsyncResponse delegate = null;*/

    public CreateAppointmentTask(String dcId, String appiontmentDate, AttachmentImagesArrayAdapter attachmentImagesArrayAdapter, Context context, Activity activity, ProgressDialog progressDialog){
        this.mDcId = dcId;
        this.context = context;
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.mAppiontmentDate = appiontmentDate;
        this.mAttachmentImagesArrayAdapter = attachmentImagesArrayAdapter;
        issue = context.getResources().getString(R.string.error_unknown_error);
        //this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("createAppointment");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
            parameters.put("dcId",mDcId);
            parameters.put("appointmentDate",mAppiontmentDate);
            parameters.put("patientId", String.valueOf(Utility.getPatientId(context)));

            // encode parameters
            Iterator entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                entries.remove();
            }

            if(!mAttachmentImagesArrayAdapter.isEmpty()){

                for(int i=0;i<mAttachmentImagesArrayAdapter.getCount();i++){
                    builder.appendQueryParameter("image[]",mAttachmentImagesArrayAdapter.getStringImage(i));
                }
            }

            String requestBody = builder.build().getEncodedQuery();
            Log.d(LOG_TAG, "Service Call URL : " + CLIENT_BASE_URL);
            Log.d(LOG_TAG, "Post parameters : " + requestBody);

            //OutputStream os = urlConnection.getOutputStream();
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(requestBody);    //bcz no parameters to be sent

            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            // Read the input stream into a String
            //InputStream inputStream = urlConnection.getInputStream();
            InputStream inputStream;
            int status = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "URL Connection Response Code " + status);

            //if(status >= 400)
            //  inputStream = urlConnection.getErrorStream();
            //else
            inputStream = urlConnection.getInputStream();


            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return false;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return false;
            }

            clientCredStr = buffer.toString();

            Log.d(LOG_TAG, "Doctor Clinics Slots Credential JSON String : " + clientCredStr);


            return fetchDoctorClinics(clientCredStr);

        } catch (IOException e) {
            Log.d(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return false;
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.d(LOG_TAG, "Error closing stream", e);
                }
            }
            //return false;
        }
    }

    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {

            //delegate.processSlotFinish(success.toString());
            progressDialog.dismiss();
            Toast.makeText(context,"Appointment Booked!",Toast.LENGTH_LONG).show();
            Intent homeIntent = new Intent(activity, HomeActivity.class);
            activity.startActivity(homeIntent);
            activity.finish();

        } else {
            progressDialog.dismiss();

                /*Snackbar.make(, R.string.error_unknown_error,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,issue,Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(activity, HomeActivity.class);
            activity.startActivity(homeIntent);
            activity.finish();

        }
    }

    private boolean fetchDoctorClinics(String clientCredStr) throws JSONException {

        final String isSuccessfullyAddedString = "successfullyAdded";
        final String errorMessageString = "errorMessage";

        JSONObject clientJson = new JSONObject(clientCredStr);
        String successFullyAdded = clientJson.getString(isSuccessfullyAddedString);
        if(successFullyAdded.contains("true")){
            return true;
        }else{
            issue = clientJson.getString(errorMessageString);
            return false;
        }

    }


}
