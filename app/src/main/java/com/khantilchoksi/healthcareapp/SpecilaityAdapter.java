package com.khantilchoksi.healthcareapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

/**
 * Created by Khantil on 24-03-2017.
 */

public class SpecilaityAdapter extends RecyclerView.Adapter<SpecilaityAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<String> specialityIdList;
    private ArrayList<String> specialityNamesList;
    private ArrayList<String> specialityDescriptionList;
    private ArrayList<String> specialityIconList;
    private Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView getSpecialityNameTextView() {
            return specialityNameTextView;
        }

        public TextView getSpecialityDescriptionTextView() {
            return specialityDescriptionTextView;
        }

        public ImageView getSpecilaityIconImageView() {
            return specilaityIconImageView;
        }

        private final TextView specialityNameTextView;
        private final TextView specialityDescriptionTextView;
        private final ImageView specilaityIconImageView;
        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(LOG_TAG," Element: "+getAdapterPosition()+ " clicked. Means "
                            +specialityNamesList.get(getAdapterPosition())+" got.");
                    String specialityId = specialityIdList.get(getAdapterPosition());
                    Intent showDoctorsIntent = new Intent(mActivity, ShowDoctorsActivity.class);
                    showDoctorsIntent.putExtra("specialityId",specialityId);
                    showDoctorsIntent.putExtra("specialityName",specialityNamesList.get(getAdapterPosition()));
                    mActivity.startActivity(showDoctorsIntent);

                }
            });

            specialityNameTextView = (TextView)itemView.findViewById(R.id.speciality_name);
            specialityDescriptionTextView = (TextView)itemView.findViewById(R.id.speciality_description);
            specilaityIconImageView = (ImageView)itemView.findViewById(R.id.speciality_icon);
        }
    }

    public SpecilaityAdapter(ArrayList<String> specialityIdList,ArrayList<String> specialityNamesList, ArrayList<String> specialityDescriptionList, ArrayList<String> specialityIconList, Activity activity) {
        this.specialityIdList = specialityIdList;
        this.specialityNamesList = specialityNamesList;
        this.specialityDescriptionList = specialityDescriptionList;
        this.specialityIconList = specialityIconList;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctor_speciality_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getSpecialityNameTextView().setText(specialityNamesList.get(position));
        holder.getSpecialityDescriptionTextView().setText(specialityDescriptionList.get(position));

        Picasso.with(mActivity.getApplicationContext()).load(specialityIconList.get(position)).
                into(holder.getSpecilaityIconImageView(), new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Success","Image loaded successfully.");
//                mIssueImagesPagerActivityFragment.showProgress(false);
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(mActivity.getApplicationContext(),"Issue downloading image!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return specialityNamesList.size();
    }


}
