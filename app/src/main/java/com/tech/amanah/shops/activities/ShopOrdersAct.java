package com.tech.amanah.shops.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityShopOrdersBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterMyOrders;
import com.tech.amanah.devliveryservices.models.ModelStoreBooking;
import com.tech.amanah.shops.adapters.AdapterShopOrders;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopOrdersAct extends AppCompatActivity {

    Context mContext = ShopOrdersAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ActivityShopOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_orders);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.ivReferesh.setOnClickListener(v -> {
            binding.etFromDate.setText("");
            binding.etToDate.setText("");
            getMyOrders();
        });

        binding.ivFilter.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etFromDate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.select_from_date), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etToDate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.select_to_date), Toast.LENGTH_SHORT).show();
            } else {
                binding.etFromDate.setText("");
                binding.etToDate.setText("");
                getfilterOrder();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.etFromDate.setOnClickListener(v -> {
            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            binding.etFromDate.setText(year + "-"
                                    + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        });

        binding.etToDate.setOnClickListener(v -> {
            // Process to get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // Display Selected date in textbox
                            binding.etToDate.setText(year + "-"
                                    + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.etFromDate.setText("");
                binding.etToDate.setText("");
                getMyOrders();
            }
        });

        getMyOrders();

    }

    private void getfilterOrder() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("shop_id", modelLogin.getResult().getId());
        param.put("from_date", binding.etFromDate.getText().toString().trim());
        param.put("to_date", binding.etToDate.getText().toString().trim());

        Log.e("sadasdadasd", "user_id = " + param);

        Call<ResponseBody> call = api.getSearchShopApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        ModelStoreBooking modelStoreBooking = new Gson().fromJson(stringResponse, ModelStoreBooking.class);
                        AdapterShopOrders adapterMyOrders = new AdapterShopOrders(mContext, modelStoreBooking.getResult());
                        ;
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                    } else {
                        AdapterShopOrders adapterMyOrders = new AdapterShopOrders(mContext, null);
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
            }
        });

    }

    private void getMyOrders() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("shop_id", modelLogin.getResult().getId());

        Log.e("sadasdadasd", "user_id = " + modelLogin.getResult().getId());

        Call<ResponseBody> call = api.myShopOrderApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        ModelStoreBooking modelStoreBooking = new Gson().fromJson(stringResponse, ModelStoreBooking.class);
                        AdapterShopOrders adapterMyOrders = new AdapterShopOrders(mContext, modelStoreBooking.getResult());
                        ;
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                    } else {
                        AdapterShopOrders adapterMyOrders = new AdapterShopOrders(mContext, null);
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
            }
        });

    }


}