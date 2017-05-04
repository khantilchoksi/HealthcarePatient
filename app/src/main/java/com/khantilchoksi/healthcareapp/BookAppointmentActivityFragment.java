package com.khantilchoksi.healthcareapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.CreateAppointmentTask;
import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetDoctorClinicSlotInfoTask;
import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetUpcomingAppointmentDateTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class BookAppointmentActivityFragment extends Fragment implements  GetDoctorClinicSlotInfoTask.AsyncResponse{

    private String LOG_TAG = getClass().getSimpleName();

    private View mRootView;

    private EditText mDoctorNameEditText;
    private EditText mDoctorQualificationsEditText;
    private EditText mDoctorSpecialitesEditText;
    private EditText mClinicNameEditText;
    private EditText mAppointmentDateEditText;
    private EditText mVisitingHoursEditText;
    private EditText mConsultationFeesEditText;
    private EditText mClinicAddressEditText;

    private Button mBookButton;

    private String mDcId;
    private String mAppointmentDate;
    private String mAppointmentDay = "Tuesday";

    final Calendar myCalendar = Calendar.getInstance();
    private ProgressDialog progressDialog;

    final DatePickerDialog.OnDateSetListener mDatePickerDateSetListener = new DatePickerDialog.OnDateSetListener() {


        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            Calendar tempCalendar = Calendar.getInstance();
            tempCalendar.set(Calendar.YEAR, year);
            tempCalendar.set(Calendar.MONTH, monthOfYear);
            tempCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String weekDay;
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

            weekDay = dayFormat.format(tempCalendar.getTime());
            Log.d(LOG_TAG,"Setting Date in Calendar: "+weekDay);
            if(weekDay.equals(mAppointmentDay)){
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateAppointmentDateTextView();
            }else{
                Toast.makeText(getContext(),"You can select only "+mAppointmentDay+" for your appointment.",Toast.LENGTH_LONG).show();
            }


        }

    };

    public BookAppointmentActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_book_appointment, container, false);

        mDcId = getActivity().getIntent().getStringExtra("dcId");

        mDoctorNameEditText = (EditText) mRootView.findViewById(R.id.doctor_name);
        mDoctorQualificationsEditText = (EditText) mRootView.findViewById(R.id.doctor_qualification);
        mDoctorSpecialitesEditText = (EditText) mRootView.findViewById(R.id.doctor_specialities);

        mClinicNameEditText = (EditText) mRootView.findViewById(R.id.clinic_name);

        mVisitingHoursEditText = (EditText) mRootView.findViewById(R.id.visiting_hours);
        mConsultationFeesEditText = (EditText) mRootView.findViewById(R.id.consultancy_fees);
        mClinicAddressEditText = (EditText) mRootView.findViewById(R.id.clinic_address);



        mAppointmentDateEditText = (EditText) mRootView.findViewById(R.id.appointment_date);
        mAppointmentDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(),mDatePickerDateSetListener,myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Appointment Info...");
        progressDialog.show();

        GetDoctorClinicSlotInfoTask getDoctorClinicSlotInfoTask = new GetDoctorClinicSlotInfoTask(mDcId,
                getContext(),getActivity(),this,progressDialog);
        getDoctorClinicSlotInfoTask.execute((Void) null);

        mBookButton = (Button) mRootView.findViewById(R.id.btn_book);
        mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookButtonClick();
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

    @Override
    public void processDoctorClinicTaskInfoFinish(String doctorName, String doctorQualificaitons,
                                                  String doctorSpecialities, String clinicName,
                                                  String appointmentDay, String visitingHours,
                                                  String consultationFees, String clinicAddress,
                                                  ProgressDialog progressDialog) {

        mDoctorNameEditText.setText(doctorName);
        mDoctorQualificationsEditText.setText(doctorQualificaitons);
        mDoctorSpecialitesEditText.setText(doctorSpecialities);

        mClinicNameEditText.setText(clinicName);
        mVisitingHoursEditText.setText(visitingHours);
        mConsultationFeesEditText.setText(consultationFees);
        mClinicAddressEditText.setText(clinicAddress);
        mAppointmentDay = appointmentDay;

        GetUpcomingAppointmentDateTask getUpcomingAppointmentDateTask = new GetUpcomingAppointmentDateTask(mDcId,
                getContext(), getActivity(), new GetUpcomingAppointmentDateTask.AsyncResponse() {
            @Override
            public void processUpcomingAppointmentDateFinish(String upcomingAppointmentDate, ProgressDialog progressDialog) {
                mAppointmentDate = upcomingAppointmentDate;
                setAppointmentDate(upcomingAppointmentDate);
                if(progressDialog!= null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }, progressDialog);
        getUpcomingAppointmentDateTask.execute((Void) null);
    }

    public void bookButtonClick(){

        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Booking Appointment...");
        progressDialog.show();

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);



        CreateAppointmentTask createAppointmentTask = new CreateAppointmentTask(
                mDcId, sdf.format(myCalendar.getTime()),
                getContext(),getActivity(), progressDialog);
        createAppointmentTask.execute((Void) null);
    }
}
