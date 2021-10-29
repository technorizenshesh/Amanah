package com.tech.amanah.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.Projection;
import com.tech.amanah.R;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.databinding.RideHistoryItemBinding;
import com.tech.amanah.taxiservices.activities.RideDetailActivity;
import com.tech.amanah.taxiservices.models.ModelTaxiHistory;

import java.util.ArrayList;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideHolder> {

    Context mContext;
    ArrayList<ModelTaxiHistory.Result> historyList;

    public RideHistoryAdapter(Context mContext, ArrayList<ModelTaxiHistory.Result> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public RideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RideHistoryItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.ride_history_item, parent, false);
        return new RideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHolder holder, int position) {

        holder.binding.setData(historyList.get(position));

        if (historyList.get(position).getStatus().equals("Cancel_by_user") || historyList.get(position).getStatus().equals("Cancel"))
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red_spalsh));

        holder.binding.tvStatus.setText(historyList.get(position)
                .getStatus().replace("_", " ").toUpperCase());

        holder.binding.ivDetails.setOnClickListener(v -> {
            ProjectUtil.blinkAnimation(holder.binding.ivDetails);
            mContext.startActivity(new Intent(mContext,RideDetailActivity.class)
            .putExtra("data",historyList.get(position))
            );
        });

    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class RideHolder extends RecyclerView.ViewHolder {

        RideHistoryItemBinding binding;

        public RideHolder(RideHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
