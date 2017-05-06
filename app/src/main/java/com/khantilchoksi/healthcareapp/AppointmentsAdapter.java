package com.khantilchoksi.healthcareapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.CancelAppointmentTask;

import java.util.ArrayList;

/**
 * Created by Khantil on 24-03-2017.
 */

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Appointment> mAppointmentsList;
    private Activity mActivity;
    private boolean mIsCancelledButtonShown;

    public class ViewHolder extends RecyclerView.ViewHolder{


        private final TextView doctorNameTextView;
        private final TextView appointmentDateTextView;
        private final TextView appointmentDayTextView;
        private final TextView appointmentStartTimeTextView;
        private final TextView appointmentEndTimeTextView;
        private final TextView clinicAddressTextView;

        public Button getCancelAppointmentButton() {
            return cancelAppointmentButton;
        }

        private final Button cancelAppointmentButton;

        private final Button viewAppointmentButton;

        public TextView getDoctorNameTextView() {
            return doctorNameTextView;
        }

        public TextView getAppointmentDateTextView() {
            return appointmentDateTextView;
        }

        public TextView getAppointmentDayTextView() {
            return appointmentDayTextView;
        }

        public TextView getAppointmentStartTimeTextView() {
            return appointmentStartTimeTextView;
        }

        public TextView getAppointmentEndTimeTextView() {
            return appointmentEndTimeTextView;
        }

        public TextView getClinicAddressTextView() {
            return clinicAddressTextView;
        }

        public ViewHolder(View itemView) {
            super(itemView);

            viewAppointmentButton = (Button) itemView.findViewById(R.id.view_appointment_button);
            viewAppointmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewIntent = new Intent(mActivity,ViewAppointmentActivity.class);
                    viewIntent.putExtra("appointmentId",
                            mAppointmentsList.get(getAdapterPosition()).getAppointmentId());
                    mActivity.startActivity(viewIntent);
                }
            });

            cancelAppointmentButton = (Button) itemView.findViewById(R.id.cancel_appointment_button);
            cancelAppointmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(mActivity,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Cancelling Appointment...");
                    progressDialog.show();
                    CancelAppointmentTask cancelAppointmentTask =
                            new CancelAppointmentTask(
                                    mAppointmentsList.get(getAdapterPosition()).getAppointmentId(),
                                    mActivity.getApplicationContext(),mActivity,progressDialog);
                    cancelAppointmentTask.execute((Void) null);
                }
            });

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG," Element: "+getAdapterPosition()+ " clicked. Means "
                            +specialityNamesList.get(getAdapterPosition())+" got.");
                    String specialityId = appointmentsList.get(getAdapterPosition());
                    Intent showDoctorsIntent = new Intent(mActivity, ShowDoctorsActivity.class);
                    showDoctorsIntent.putExtra("specialityId",specialityId);
                    showDoctorsIntent.putExtra("specialityName",specialityNamesList.get(getAdapterPosition()));
                    mActivity.startActivity(showDoctorsIntent);

                }
            });*/

            doctorNameTextView = (TextView)itemView.findViewById(R.id.doctor_name_text_view);
            appointmentDateTextView = (TextView)itemView.findViewById(R.id.appointment_date);
            appointmentDayTextView = (TextView) itemView.findViewById(R.id.appointment_day);
            appointmentStartTimeTextView = (TextView) itemView.findViewById(R.id.appointment_start_time_text_view);
            appointmentEndTimeTextView = (TextView) itemView.findViewById(R.id.appointment_end_time_text_view);
            clinicAddressTextView = (TextView)itemView.findViewById(R.id.clinic_address_text_view);
        }
    }

    public AppointmentsAdapter(ArrayList<Appointment> appointmentsList, boolean isCancelledButtonShown,Activity activity) {
        this.mAppointmentsList = appointmentsList;
        this.mIsCancelledButtonShown = isCancelledButtonShown;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        if(!mIsCancelledButtonShown){
            //remove cancel button
            holder.getCancelAppointmentButton().setVisibility(View.INVISIBLE);
        }
        holder.getDoctorNameTextView().setText(mAppointmentsList.get(position).getDoctorName());
        holder.getAppointmentDateTextView().setText(mAppointmentsList.get(position).getAppointmentDate());
        holder.getAppointmentDayTextView().setText(mAppointmentsList.get(position).getAppointmentDay());
        holder.getAppointmentStartTimeTextView().setText(mAppointmentsList.get(position).getAppointmentStartTime());
        holder.getAppointmentEndTimeTextView().setText(mAppointmentsList.get(position).getAppointmentEndTime());
        holder.getClinicAddressTextView().setText(mAppointmentsList.get(position).getClinicAddress());

    }

    @Override
    public int getItemCount() {
        return mAppointmentsList.size();
    }


}
