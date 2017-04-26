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

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.GetDoctorsFromSpecialityTask;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowDoctorsActivityFragment extends Fragment implements GetDoctorsFromSpecialityTask.AsyncResponse{

    private View mRootView;
    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Doctor> mDocotorsList;
    private DoctorsRecyclerAdapter mDoctorsRecyclerAdapter;
    private String mSpecialityId;
    private LinearLayout mNoDoctorsAvailableLayout;

    public ShowDoctorsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_show_doctors, container, false);

        mSpecialityId = getActivity().getIntent().getStringExtra("specialityId");

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.doctors_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);

        mNoDoctorsAvailableLayout = (LinearLayout) mRootView.findViewById(R.id.no_doctors_available_layout);
        getDoctors();

        return mRootView;
    }

    public void getDoctors(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Doctors Info...");
        progressDialog.show();

        GetDoctorsFromSpecialityTask getClinicsTask = new GetDoctorsFromSpecialityTask(
                getContext(),getActivity(),mSpecialityId,this,progressDialog);

        getClinicsTask.execute((Void) null);
    }

    @Override
    public void processFinish(ArrayList<Doctor> doctorsList, ProgressDialog progressDialog) {
        this.mDocotorsList = doctorsList;

        if(mDocotorsList.isEmpty()){
            mNoDoctorsAvailableLayout.setVisibility(View.VISIBLE);
        }else{
            mDoctorsRecyclerAdapter = new DoctorsRecyclerAdapter(mDocotorsList,getContext(),getActivity());
            mRecyclerView.setAdapter(mDoctorsRecyclerAdapter);
        }
        this.progressDialog = progressDialog;
        this.progressDialog.dismiss();
    }
}
