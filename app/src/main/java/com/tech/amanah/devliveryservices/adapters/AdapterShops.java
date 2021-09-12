package com.tech.amanah.devliveryservices.adapters;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tech.amanah.R;
import com.tech.amanah.databinding.AdapterAllShopsBinding;
import com.tech.amanah.databinding.AdapterShopTypeListBinding;
import com.tech.amanah.devliveryservices.activities.ShopItemsAct;
import com.tech.amanah.devliveryservices.activities.ShopListAct;
import com.tech.amanah.devliveryservices.models.ModelShopCat;
import com.tech.amanah.devliveryservices.models.ModelShopList;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class AdapterShops extends RecyclerView.Adapter<AdapterShops.ViewHolderShopType> {

    Context mContext;
    ArrayList<ModelShopList.Result> catList;

    public AdapterShops(Context mContext, ArrayList<ModelShopList.Result> catList) {
        this.mContext = mContext;
        this.catList = catList;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterShops.ViewHolderShopType onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        AdapterAllShopsBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext), R.layout.adapter_all_shops,parent,false);
        return new AdapterShops.ViewHolderShopType(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterShops.ViewHolderShopType holder, int position) {
        holder.binding.setData(catList.get(position));
        holder.binding.tvDistance.setText(catList.get(position).getDistance() + " Km Far");

        Log.e("asdfadasd","catList Shop Front = " + catList.get(position).getId());

        Glide.with(mContext)
                .load(catList.get(position).getShop_front_image())
                .into(holder.binding.ivImage);

//        Picasso.get()
//                .load(catList.get(position).getShop_front_image())
//                .resize(500,500)
//                .into(holder.binding.ivImage);

        holder.binding.getRoot().setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ShopItemsAct.class)
            .putExtra("id",catList.get(position).getUser_id())
            .putExtra("name",catList.get(position).getShop_name())
            .putExtra("storeId",catList.get(position).getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return catList==null?0:catList.size();
    }

    public class ViewHolderShopType extends RecyclerView.ViewHolder {

        AdapterAllShopsBinding binding;

        public ViewHolderShopType(@NonNull @NotNull AdapterAllShopsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
