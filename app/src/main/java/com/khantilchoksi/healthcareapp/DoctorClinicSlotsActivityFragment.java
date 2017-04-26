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

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetDoctorClinicSlotsTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DoctorClinicSlotsActivityFragment extends Fragment implements GetDoctorClinicSlotsTask.AsyncResponse{

    private View mRootView;
    private RecyclerView mRecyclerView;
    private ClinicRecyclerAdapter mClinicsRecyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Clinic> mClinicsList;
    private String mDoctorId;

    private LinearLayout mNoClinicsLinearLayout;

    private ProgressDialog progressDialog;

    public DoctorClinicSlotsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_doctor_clinic_slots, container, false);

        mDoctorId = getActivity().getIntent().getStringExtra("doctorId");

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.clinics_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.scrollToPosition(scrollPosition);

        mNoClinicsLinearLayout = (LinearLayout) mRootView.findViewById(R.id.no_clinics_available_layout);

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initDataset();
    }

    private void initDataset() {
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Clinics Details...");
        progressDialog.show();

        GetDoctorClinicSlotsTask getDoctorClinicSlotsTask = new GetDoctorClinicSlotsTask(mDoctorId,getContext(),
                getActivity(), this, progressDialog);
        getDoctorClinicSlotsTask.execute((Void) null);
    }

    @Override
    public void processFinish(ArrayList<Clinic> clinicsList, ProgressDialog progressDialog) {
        this.mClinicsList = clinicsList;


        if(mClinicsList.isEmpty()){
            mNoClinicsLinearLayout.setVisibility(View.VISIBLE);
        }else{
            mClinicsRecyclerAdapter = new ClinicRecyclerAdapter(mDoctorId,this.mClinicsList, getContext(), getActivity());
            mRecyclerView.setAdapter(mClinicsRecyclerAdapter);
        }
        progressDialog.dismiss();
    }
}
