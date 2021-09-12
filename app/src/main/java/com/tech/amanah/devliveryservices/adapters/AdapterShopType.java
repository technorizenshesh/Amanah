package com.tech.amanah.devliveryservices.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tech.amanah.R;
import com.tech.amanah.databinding.AdapterShipChatBinding;
import com.tech.amanah.databinding.AdapterShopTypeListBinding;
import com.tech.amanah.devliveryservices.activities.ShopListAct;
import com.tech.amanah.devliveryservices.models.ModelShopCat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterShopType extends RecyclerView.Adapter<AdapterShopType.ViewHolderShopType> {

    Context mContext;
    ArrayList<ModelShopCat.Result> catList;

    public AdapterShopType(Context mContext, ArrayList<ModelShopCat.Result> catList) {
        this.mContext = mContext;
        this.catList = catList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolderShopType onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        AdapterShopTypeListBinding binding = DataBindingUtil.
                inflate(LayoutInflater.from(mContext),R.layout.adapter_shop_type_list,parent,false);
        return new ViewHolderShopType(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolderShopType holder, int position) {
        ModelShopCat.Result data = catList.get(position);

        holder.binding.tvTypeName.setText(data.getCategory_name());

        holder.binding.itemView.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, ShopListAct.class)
            .putExtra("id",data.getId())
            );
        });

    }

    @Override
    public int getItemCount() {
        return catList==null?0:catList.size();
    }

    public class ViewHolderShopType extends RecyclerView.ViewHolder {

        AdapterShopTypeListBinding binding;

        public ViewHolderShopType(@NonNull @NotNull AdapterShopTypeListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
