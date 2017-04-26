package com.khantilchoksi.healthcareapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.khantilchoksi.healthcareapp.ArztAsyncCalls.CreateAppointmentTask;

import java.util.ArrayList;

/**
 * Created by Khantil on 24-03-2017.
 */

public class SlotsRecyclerAdapter extends RecyclerView.Adapter<SlotsRecyclerAdapter.ViewHolder> {


    private final String LOG_TAG = getClass().getSimpleName();

    private ArrayList<Slot> mSlotsList;
    private Context mContext;
    private Activity mActivity;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView slotDayTextView;
        private TextView slotStartTimeTextView;
        private Button bookButton;

        public TextView getSlotDayTextView() {
            return slotDayTextView;
        }

        public TextView getSlotStartTimeTextView() {
            return slotStartTimeTextView;
        }

        public TextView getSlotEndTimeTextView() {
            return slotEndTimeTextView;
        }

        public TextView getSlotFeesTextView() {
            return slotFeesTextView;
        }

        private TextView slotEndTimeTextView;
        private TextView slotFeesTextView;



        public ViewHolder(View itemView) {
            super(itemView);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });*/

            bookButton = (Button) itemView.findViewById(R.id.btn_book);
            bookButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog progressDialog = new ProgressDialog(mActivity,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Booking Appointment...");
                    progressDialog.show();
                    CreateAppointmentTask createAppointmentTask = new CreateAppointmentTask(
                            mSlotsList.get(getAdapterPosition()).getSlotId(),
                            mContext,mActivity, progressDialog);
                    createAppointmentTask.execute((Void) null);
                }
            });

            slotDayTextView = (TextView)itemView.findViewById(R.id.slot_day_text_view);
            slotStartTimeTextView = (TextView)itemView.findViewById(R.id.slot_start_time_text_view);
            slotEndTimeTextView = (TextView) itemView.findViewById(R.id.slot_end_time_text_view);
            slotFeesTextView = (TextView) itemView.findViewById(R.id.slot_fees_text_view);

        }
    }

    public SlotsRecyclerAdapter(ArrayList<Slot> slotsList, Context context, Activity activity) {
        this.mSlotsList = slotsList;
        this.mContext = context;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Slot Day Row Item Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        holder.getSlotDayTextView().setText(mSlotsList.get(position).getDay());
        holder.getSlotStartTimeTextView().setText(mSlotsList.get(position).getStartTime());
        holder.getSlotEndTimeTextView().setText(mSlotsList.get(position).getEndTime());
        holder.getSlotFeesTextView().setText(mContext.getResources().getString(R.string.rupee_symbol).
                concat(" "+mSlotsList.get(position).getSlotFees()));
    }

    @Override
    public int getItemCount() {
        return mSlotsList.size();
    }


}
