package com.khantilchoksi.healthcareapp.ArztAsyncCalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.khantilchoksi.healthcareapp.R;
import com.khantilchoksi.healthcareapp.Doctor;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Khantil on 22-03-2017.
 */

public class GetDoctorsFromSpecialityTask extends AsyncTask<Void, Void, Boolean> {

    private final String LOG_TAG = getClass().getSimpleName();
    Context context;
    Activity activity;
    String mSpecialityId;
    ArrayList<Doctor> doctorsList;
    String issue;
    ProgressDialog progressDialog;

    public interface AsyncResponse {
        void processFinish(ArrayList<Doctor> doctorsList, ProgressDialog progressDialog);
    }

    public AsyncResponse delegate = null;

    public GetDoctorsFromSpecialityTask(Context context, Activity activity, String specialityId, AsyncResponse delegate, ProgressDialog progressDialog){
        this.context = context;
        this.activity = activity;
        this.delegate = delegate;
        this.progressDialog = progressDialog;
        this.mSpecialityId = specialityId;
        doctorsList = new ArrayList<Doctor>();
        issue = context.getResources().getString(R.string.error_unknown_error);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String clientCredStr;

        try {

            final String CLIENT_BASE_URL = context.getResources().getString(R.string.base_url).concat("getDoctorsFromSpeciality");
            URL url = new URL(CLIENT_BASE_URL);


            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            Uri.Builder builder = new Uri.Builder();
            Map<String, String> parameters = new HashMap<>();
                parameters.put("specialityId", mSpecialityId);

            // encode parameters
            Iterator entries = parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                entries.remove();
            }
            String requestBody = builder.build().getEncodedQuery();
            Log.d(LOG_TAG, "Service Call URL : " + CLIENT_BASE_URL);
            //Log.d(LOG_TAG, "Post parameters : " + requestBody);

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

            Log.d(LOG_TAG, "Clinics Credential JSON String : " + clientCredStr);


            return getAllDoctors(clientCredStr);

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
        progressDialog.dismiss();
        Toast.makeText(context,issue,Toast.LENGTH_SHORT).show();
        //go to parent activity remaining
    }

    @Override
    protected void onPostExecute(Boolean success) {
        Log.d(LOG_TAG, "Success Boolean Tag: " + success.toString());
        if (success) {

            delegate.processFinish(doctorsList, progressDialog);

        } else {

            progressDialog.dismiss();


                /*Snackbar.make(context, issue,
                        Snackbar.LENGTH_LONG)
                        .show();*/
            Toast.makeText(context,issue,Toast.LENGTH_SHORT).show();

        }
    }

    private boolean getAllDoctors(String clientCredStr) throws JSONException {

        //final String isClinicsAvailableString = "clinicsAvailable";
        final String doctorsListString = "doctorList";
        final String doctorIdString = "doctorId";
        final String doctorNameString = "doctorName";
        final String clinicAreasListString = "clinicAreasList";
        final String doctorQualificationsString = "doctorQualifications";
        final String doctorSpecialitiesString = "doctorSpecialities";
        final String doctorExperienceString = "experience";


        String tempId;
        String tempName;
        ArrayList<String> tempAreas;
        String tempQualifications;
        String tempSpecialities;
        int tempExperience;


        JSONObject clientJson = new JSONObject(clientCredStr);

        JSONArray doctorsJsonArray = clientJson.getJSONArray(doctorsListString);
        if(doctorsJsonArray != null){

                for(int i=0;i<doctorsJsonArray.length();i++){
                    JSONObject doctorJSONObject = doctorsJsonArray.getJSONObject(i);
                    tempId = doctorJSONObject.getString(doctorIdString);
                    tempName = doctorJSONObject.getString(doctorNameString);

                    tempQualifications = doctorJSONObject.getString(doctorQualificationsString);
                    tempSpecialities = doctorJSONObject.getString(doctorSpecialitiesString);

                    tempExperience = doctorJSONObject.getInt(doctorExperienceString);

                    JSONArray areasJsonAray = doctorJSONObject.getJSONArray(clinicAreasListString);
                    tempAreas = new ArrayList<String>();
                    for(int j=0;j<areasJsonAray.length();j++){
                        tempAreas.add(areasJsonAray.getString(j));
                    }

                    doctorsList.add(new Doctor(tempId,tempName,tempExperience,
                            tempQualifications,tempSpecialities, tempAreas));

                    Log.d(LOG_TAG,"ID : "+tempId+"Name: "+tempName);
                }

                return true;


        }else{
            issue = context.getResources().getString(R.string.no_dotors_available);
            return false;
        }

    }


}
