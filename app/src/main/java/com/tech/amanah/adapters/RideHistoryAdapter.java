package com.tech.amanah.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;
import com.tech.amanah.taxiservices.activities.RideDetailActivity;

/**
 * Created by Ravindra Birla on 19,February,2021
 */
public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideHistoryViewHolder> {

    Context context;

    public RideHistoryAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public RideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.ride_history_item, parent, false);
        RideHistoryViewHolder viewHolder = new RideHistoryViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class RideHistoryViewHolder extends RecyclerView.ViewHolder
    {

        LinearLayout llRideDetail;

        public RideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            llRideDetail = itemView.findViewById(R.id.llRideDetail);
            llRideDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, RideDetailActivity.class));

                }
            });

        }
    }

}
