package com.tech.amanah.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;


/**
 * Created by Ravindra Birla on 22,February,2021
 */
public class IssuesAdapter extends RecyclerView.Adapter<IssuesAdapter.IssueViewHolder>{

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.car_item, parent, false);
        IssueViewHolder viewHolder = new IssueViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class IssueViewHolder extends RecyclerView.ViewHolder

    {

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
