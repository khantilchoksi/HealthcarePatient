package com.khantilchoksi.healthcareapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetPastAppointmentsTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class AppointmentsHistoryActivityFragment extends Fragment implements GetPastAppointmentsTask.AsyncResponse{
    private View mRootview;
    private RecyclerView mRecyclerView;
    private AppointmentsAdapter mAppointmentAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Appointment> mAppointmentsList;
    private LinearLayout mNoAppointmentsLinearLayout;

    private ProgressDialog progressDialog;

    public AppointmentsHistoryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootview = inflater.inflate(R.layout.fragment_appointments_history, container, false);

        mNoAppointmentsLinearLayout = (LinearLayout) mRootview.findViewById(R.id.no_appointments_available_layout);
        initDataset();
        mRecyclerView = (RecyclerView) mRootview.findViewById(R.id.appointments_history_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return mRootview;
    }

    private void initDataset() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Past Appointments...");
        progressDialog.show();

        GetPastAppointmentsTask getPastAppointmentsTask = new GetPastAppointmentsTask(getContext(),
                getActivity(),this,progressDialog);
        getPastAppointmentsTask.execute((Void) null);
    }

    @Override
    public void processFinish(ArrayList<Appointment> appointmentsList, ProgressDialog progressDialog) {
        this.mAppointmentsList = appointmentsList;


        if(mAppointmentsList.isEmpty()){
            mNoAppointmentsLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mAppointmentAdapter = new AppointmentsAdapter(this.mAppointmentsList, getActivity());
            mRecyclerView.setAdapter(mAppointmentAdapter);
        }
        progressDialog.dismiss();
    }
}
