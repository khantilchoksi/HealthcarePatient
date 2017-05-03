package com.khantilchoksi.healthcareapp;

/**
 * Created by khantilchoksi on 28/03/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DoctorsRecyclerAdapter extends RecyclerView.Adapter<DoctorsRecyclerAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Doctor> doctorsList;
    private Context mContext;
    private Activity mActivity;


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView getDoctorNameTextView() {
            return doctorNameTextView;
        }

        public TextView getDoctorClinicAreasTextView() {
            return doctorClinicAreasTextView;
        }



        private final TextView doctorNameTextView;
        private final TextView doctorClinicAreasTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG," Clinic: "+getAdapterPosition()+ " clicked. Means "
                            + doctorsList.get(getAdapterPosition()).getDoctorName()+" got.");

                    Intent doctorClinicIntent = new Intent(mActivity,DoctorClinicSlotsActivity.class);
                    doctorClinicIntent.putExtra("doctorId",doctorsList.get(getAdapterPosition()).getDoctorId());
                    mActivity.startActivity(doctorClinicIntent);

                }
            });

            doctorNameTextView = (TextView)itemView.findViewById(R.id.doctor_name_text_view);
            doctorClinicAreasTextView = (TextView)itemView.findViewById(R.id.doctor_visiting_areas_text_view);

        }
    }

    public DoctorsRecyclerAdapter(ArrayList<Doctor> doctorsList, Context context, Activity activity) {
        this.doctorsList = doctorsList;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.show_doctor_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getDoctorNameTextView().setText(doctorsList.get(position).getDoctorName());
        holder.getDoctorClinicAreasTextView().setText(android.text.TextUtils.join(",", doctorsList.get(position).getClinicsAreaList()));
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }


}

