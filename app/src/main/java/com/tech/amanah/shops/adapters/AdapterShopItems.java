package com.tech.amanah.shops.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.TouchImageView;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.adapters.CarAdapter;
import com.tech.amanah.databinding.AdapterMyProductsBinding;
import com.tech.amanah.databinding.ItemRideBookBinding;
import com.tech.amanah.devliveryservices.models.ModelShopCat;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelCar;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterShopItems extends RecyclerView.Adapter<AdapterShopItems.MyViewHolder> {

    Context mContext;
    ArrayList<ModelShopItems.Result> shopItemsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterShopItems(Context mContext, ArrayList<ModelShopItems.Result> shopItemsList) {
        this.mContext = mContext;
        this.shopItemsList = shopItemsList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterShopItems.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMyProductsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_my_products, parent, false);
        return new AdapterShopItems.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShopItems.MyViewHolder holder, int position) {

        holder.binding.setData(shopItemsList.get(position));

        Glide.with(mContext).load(shopItemsList.get(position).getItem_image()).into(holder.binding.ivImage);

        holder.binding.tvPrice.setText(AppConstant.CURRENCY +" "+ shopItemsList.get(position).getItem_price());

        holder.binding.ivDelete.setOnClickListener(v -> {
            deleteItemApi(position,shopItemsList.get(position).getId());
        });

        holder.binding.ivImage.setOnClickListener(v -> {
            imageShowFullscreenDialog(shopItemsList.get(position).getItem_image());
        });

    }

    private void imageShowFullscreenDialog(String url) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.image_fullscreen_dialog);

        TouchImageView ivImage = dialog.findViewById(R.id.ivImage);
        ivImage.setMaxZoom(4f);

        Glide.with(mContext)
                .load(url)
                .into(ivImage);

        dialog.show();
    }

    private void deleteItemApi(int position,String itemId) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("item_id",itemId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteShopItems(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("deleteShopItems","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        shopItemsList.remove(position);
                        notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception","Throwable = " +t.getMessage());
            }
        });

    }


    @Override
    public int getItemCount() {
        return shopItemsList == null?0: shopItemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterMyProductsBinding binding;

        public MyViewHolder(@NonNull AdapterMyProductsBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

    }

}
