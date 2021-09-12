package com.tech.amanah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;
import com.tech.amanah.databinding.ItemPaymentBinding;
import com.vinaygaba.creditcardview.CardNumberFormat;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    Context context;


    public PaymentAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

       ItemPaymentBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(context), R.layout.item_payment, parent, false);

        return new MyViewHolder(binding);
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


       // binding.card1.setCardNumber(topicDataModels.get(position).cardNumber);
       // binding.card1.setExpiryDate(topicDataModels.get(position).expiryMonth+"/"+topicDataModels.get(position).expiryDate);

        holder.binding.card1.setCardNumberFormat(CardNumberFormat.MASKED_ALL_BUT_LAST_FOUR);
    }

    @Override
    public int getItemCount() {
      return 3;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemPaymentBinding binding;
        public MyViewHolder( ItemPaymentBinding itemView ) {
            super(itemView.getRoot());
            binding = itemView;

        }



    }
}
