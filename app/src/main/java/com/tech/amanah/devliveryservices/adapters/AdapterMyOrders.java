package com.tech.amanah.devliveryservices.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.activities.TrackDriverAct;
import com.tech.amanah.databinding.AdapterDevOrdersBinding;
import com.tech.amanah.databinding.AdapterItemDetailsBinding;
import com.tech.amanah.databinding.FoodOrderDetailDialogBinding;
import com.tech.amanah.devliveryservices.models.ModelStoreBooking;

import java.util.ArrayList;

public class AdapterMyOrders extends RecyclerView.Adapter<AdapterMyOrders.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreBooking.Result> cartList;

    public AdapterMyOrders(Context mContext, ArrayList<ModelStoreBooking.Result> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public AdapterMyOrders.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterDevOrdersBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_dev_orders, parent, false);
        return new AdapterMyOrders.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyOrders.StoreHolder holder, int position) {
        ModelStoreBooking.Result data = cartList.get(position);

        Glide.with(mContext).load(data.getShop_front_image()).into(holder.binding.ivStoreImage);

        if ("Confirmed".equals(data.getStatus())) {
            holder.binding.tvTrack.setVisibility(View.GONE);
            holder.binding.tvStatus.setText(mContext.getString(R.string.order_confirmed_text));
        } else if ("Accept".equals(data.getStatus())) {
            holder.binding.tvTrack.setVisibility(View.VISIBLE);
            holder.binding.tvStatus.setText(mContext.getString(R.string.order_accept_text));
        } else if ("Pickup".equals(data.getStatus())) {
            holder.binding.tvTrack.setVisibility(View.VISIBLE);
            holder.binding.tvStatus.setText(mContext.getString(R.string.order_pickup_text));
        } else if ("Delivered".equals(data.getStatus())) {
            holder.binding.tvTrack.setVisibility(View.GONE);
            holder.binding.tvStatus.setText(mContext.getString(R.string.order_devlivered_text));
        } else if ("Pending".equals(data.getStatus())) {
            holder.binding.tvTrack.setVisibility(View.GONE);
            holder.binding.tvStatus.setText(mContext.getString(R.string.order_waiting_text));
        }

        holder.binding.tvStoreName.setText(data.getShop_name());
        holder.binding.tvStoreAddress.setText(data.getShop_address());
        holder.binding.tvOrderId.setText(mContext.getString(R.string.order_id) + " : " + data.getOrder_id());
        holder.binding.tvDate.setText(data.getBook_date());
        holder.binding.tvTime.setText(data.getBook_time());
        holder.binding.tvOrderAmt.setText(AppConstant.CURRENCY + " " + data.getAmount());

        Log.e("hdfgdsgdsf","qr_image = " + data.getQr_image());

        holder.binding.tvTrack.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext,TrackDriverAct.class)
                    .putExtra("driver_id",data.getDriver_id())
            );
        });

        holder.binding.btnQR.setOnClickListener(v -> {
            ProjectUtil.imageShowFullscreenDialog(mContext,data.getQr_image());
        });

        holder.binding.tvDetails.setOnClickListener(v -> {
            openOrderDetailDialog(data);
        });

    }

    private void openOrderDetailDialog(ModelStoreBooking.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        FoodOrderDetailDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.food_order_detail_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.itemsTotal.setText(AppConstant.CURRENCY + " " + data.getAmount());
        dialogBinding.tvDevAddress.setText(data.getAddress());
        dialogBinding.tvOrderId.setText(data.getOrder_id());
        dialogBinding.payType.setText(data.getPayment_type());

        if(data.getName() == null || data.getName().equals("")) {
            dialogBinding.cusName.setText(data.getDriver_name());
        } else {
            dialogBinding.cusName.setText(data.getName());
        }

        dialogBinding.ivCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext,data.getDriver_mobile());
        });

        AdapterOrderItems adapterOrderItems = new AdapterOrderItems(mContext, data.getCart_list());
        dialogBinding.rvItems.setAdapter(adapterOrderItems);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return cartList == null ? 0 : cartList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder {

        AdapterDevOrdersBinding binding;

        public StoreHolder(AdapterDevOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
