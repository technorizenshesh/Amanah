package com.tech.amanah.devliveryservices.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.AdapterMyCartStoreBinding;
import com.tech.amanah.devliveryservices.activities.MyCartActivity;
import com.tech.amanah.devliveryservices.models.ModelMyStoreCart;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterMyCart extends RecyclerView.Adapter<AdapterMyCart.StoreCatHolder> {

    Context mContext;
    ArrayList<ModelMyStoreCart.Result> itemsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    double itemTotal = 0.0;
    int itemCount = 0;

    public AdapterMyCart(Context mContext, ArrayList<ModelMyStoreCart.Result> itemsList) {
        this.mContext = mContext;
        this.itemsList = itemsList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterMyCart.StoreCatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterMyCartStoreBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_my_cart_store,parent,false);
        return new AdapterMyCart.StoreCatHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyCart.StoreCatHolder holder, int position) {

        ModelMyStoreCart.Result data = itemsList.get(position);

        holder.binding.tvName.setText(data.getItem_name());
        holder.binding.tvPrice.setText(AppConstant.CURRENCY+" " + data.getItem_price() +" x "+ data.getQuantity());
        Glide.with(mContext).load(data.getItem_image()).into(holder.binding.ivImage);

        holder.binding.ivDelete.setOnClickListener(v -> {
            Log.e("fsffddsfds","data.getId() = " + data.getId());
            deleteApiCall(data.getCart_id(),position);
        });

    }

    private void deleteApiCall(String id,int position) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("cart_id",id);

        Log.e("sdadasdasdf",param.toString());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteCartItems(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        
                        Log.e("responseString","response = " + response);
                        Log.e("responseString","responseString = " + responseString);

                        itemsList.remove(position);
                        notifyDataSetChanged();
                        ((MyCartActivity)mContext).updateCartId(itemsList);

                        if(itemsList == null || itemsList.size() == 0) {
                            ((MyCartActivity)mContext).finish();
                        }

                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void addToCartApi(Dialog dialog, ModelMyStoreCart.Result data, String quantity) {
        ProjectUtil.showProgressDialog(mContext,false,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        // param.put("shop_id",data.getRestaurant_id());
        param.put("item_id",data.getId());
        param.put("quantity",quantity);

        Log.e("paramparam","param = " + param.toString());

        Call<ResponseBody> call = api.addToCartApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Log.e("fsdfdsfdsf","responseString = " + responseString);
                        Log.e("fsdfdsfdsf","response = " + response);
                        Log.e("fsdfdsfdsf","count = " + jsonObject.getInt("count"));
                        ((MyCartActivity)mContext).updateCartCount();
                        dialog.dismiss();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("fsdfdsfdsf","response = " + t.getMessage());
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public int getItemCount() {
        return itemsList == null?0:itemsList.size();
    }

    public class StoreCatHolder extends RecyclerView.ViewHolder{

        AdapterMyCartStoreBinding binding;

        public StoreCatHolder(AdapterMyCartStoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }


}

