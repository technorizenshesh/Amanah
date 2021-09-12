package com.tech.amanah.devliveryservices.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.databinding.AdapterItemDetailsBinding;
import com.tech.amanah.devliveryservices.models.ModelStoreBooking;

import java.util.ArrayList;

public class AdapterOrderItems extends RecyclerView.Adapter<AdapterOrderItems.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreBooking.Result.Cart_list> cartList;

    public AdapterOrderItems(Context mContext, ArrayList<ModelStoreBooking.Result.Cart_list> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public AdapterOrderItems.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemDetailsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_item_details,parent,false);
        return new StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderItems.StoreHolder holder, int position) {
        ModelStoreBooking.Result.Cart_list data = cartList.get(position);

        holder.binding.tvName.setText(data.getItem_name());
        holder.binding.tvPrice.setText(AppConstant.CURRENCY +" "+ data.getItem_price() +" x "+ data.getQuantity());

        Glide.with(mContext).load(data.getItem_image()).into(holder.binding.ivImage);

        holder.binding.ivImage.setOnClickListener(v -> {
            ProjectUtil.imageShowFullscreenDialog(mContext,data.getItem_image());
        });

    }

    @Override
    public int getItemCount() {
        return cartList == null?0:cartList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterItemDetailsBinding binding;

        public StoreHolder(AdapterItemDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
