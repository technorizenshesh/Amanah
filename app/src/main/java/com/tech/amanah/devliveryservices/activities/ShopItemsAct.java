package com.tech.amanah.devliveryservices.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityShopItemsBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterDevShopItems;
import com.tech.amanah.shops.adapters.AdapterShopItems;
import com.tech.amanah.shops.models.ModelShopItems;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopItemsAct extends AppCompatActivity {

    Context mContext = ShopItemsAct.this;
    ActivityShopItemsBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String id,name,storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shop_items);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        storeId = getIntent().getStringExtra("storeId");

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        itit();

    }

    private void itit() {

        binding.shopName.setText(name);

        getShopItemsApiCall();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyCartActivity.class));
        });

        binding.rlCounter.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyCartActivity.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    private void getShopItemsApiCall() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllShopItems(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getShopItemsApiCall","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelShopItems modelShopItems = new Gson().fromJson(responseString,ModelShopItems.class);

                        AdapterDevShopItems adapterDevShopItems = new AdapterDevShopItems(mContext,modelShopItems.getResult(),storeId);
                        binding.rvShopCat.setAdapter(adapterDevShopItems);

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

    public void getCartCount() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getCartCountApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("fsdfdsfds","response = " + jsonObject.getInt("count"));
                    Log.e("fsdfdsfds",responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        binding.tvCartCount.setText(String.valueOf(jsonObject.getInt("count")));
                    } else {
                        binding.tvCartCount.setText("0");
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


}