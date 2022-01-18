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
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.databinding.ActivityShopListBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterShopType;
import com.tech.amanah.devliveryservices.models.ModelShopCat;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopTypeListAct extends AppCompatActivity {

    Context mContext = ShopTypeListAct.this;
    ActivityShopListBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shop_list);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {
        
        getCategoryApiCall();

        binding.rlCart.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyCartActivity.class));
        });

    }

    private void getCategoryApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getShopCategory();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if(jsonObject.getString("status").equals("1")) {
                            Log.e("asfddasfasdf","response = " + response);
                            ModelShopCat modelShopCat = new Gson().fromJson(stringResponse,ModelShopCat.class);
                            AdapterShopType adapterShopType = new AdapterShopType(mContext,modelShopCat.getResult());
                            binding.rvShopCat.setAdapter(adapterShopType);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception-->",t.getMessage());
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartCount();
    }

    private void getCartCount() {
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