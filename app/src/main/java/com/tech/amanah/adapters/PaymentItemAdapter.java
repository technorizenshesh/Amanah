package com.tech.amanah.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;

/**
 * Created by Ravindra Birla on 18,February,2021
 */
public class PaymentItemAdapter extends RecyclerView.Adapter<PaymentItemAdapter.PaymentItemViewHolder>{

    Context context;

    public PaymentItemAdapter(Context context)
    {
        this.context = context;
    }


    @NonNull
    @Override
    public PaymentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.payment_item, parent, false);
        PaymentItemViewHolder viewHolder = new PaymentItemViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentItemViewHolder holder, int position) {

        if(position==1)
        {
            holder.ivCard.setImageResource(R.drawable.visa);
        }

        if(position==2)
        {
            holder.ivCard.setImageResource(R.drawable.ic_payment);
            holder.tvCardNumber.setText("Cash");
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class PaymentItemViewHolder extends RecyclerView.ViewHolder
    {

         RelativeLayout rlPayment;
         ImageView ivCard;
         TextView tvCardNumber;
        public PaymentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            rlPayment = itemView.findViewById(R.id.rlPayment);

            ivCard = itemView.findViewById(R.id.ivCard);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);

            rlPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // context.startActivity(new Intent(context, SelectDriverActivity.class));

                }
            });
        }
    }
}
