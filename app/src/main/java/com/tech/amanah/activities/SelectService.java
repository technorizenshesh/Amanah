package com.tech.amanah.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.adapters.AdapterSupports;
import com.tech.amanah.databinding.ActivitySelectServiceBinding;
import com.tech.amanah.databinding.CompAccountDialogBinding;
import com.tech.amanah.databinding.FoodOrderDetailDialogBinding;
import com.tech.amanah.databinding.SupportDialogBinding;
import com.tech.amanah.devliveryservices.activities.ShopTypeListAct;
import com.tech.amanah.devliveryservices.adapters.AdapterMyOrders;
import com.tech.amanah.devliveryservices.adapters.AdapterOrderItems;
import com.tech.amanah.devliveryservices.models.ModelStoreBooking;
import com.tech.amanah.devliveryservices.models.ModelSupport;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectService extends AppCompatActivity {

    ActivitySelectServiceBinding binding;
    Context mContext = SelectService.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_select_service);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyOrders();
            }
        });

        getMyOrders();

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.cvTaxi.setOnClickListener(v -> {
//        Intent i = new Intent(SelectService.this, TaxiHomeAct.class);
//        startActivity(i);
        });

        binding.cvDelivery.setOnClickListener(v -> {
            Intent i = new Intent(SelectService.this, ShopTypeListAct.class);
            startActivity(i);
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnCompAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            compAccDialog();
        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            supportApi();
        });

        binding.childNavDrawer.btnOrders.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,MyOrdersAct.class));
        });

        binding.childNavDrawer.btnChangePass.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("Job_Status_Action")) {
                newMyOrders();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newMyOrders();
        registerReceiver(broadcastReceiver,new IntentFilter("Job_Status_Action"));
    }

    private void newMyOrders() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Log.e("sadasdadasd","user_id = " + modelLogin.getResult().getId());

        Call<ResponseBody> call = api.myOrderApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelStoreBooking modelStoreBooking = new Gson().fromJson(stringResponse,ModelStoreBooking.class);
                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,modelStoreBooking.getResult());;
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                    } else {
                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,null);
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
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
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Log.e("sadasdadasd","user_id = " + modelLogin.getResult().getId());

        Call<ResponseBody> call = api.myOrderApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelStoreBooking modelStoreBooking = new Gson().fromJson(stringResponse,ModelStoreBooking.class);
                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,modelStoreBooking.getResult());;
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
                    } else {
                        AdapterMyOrders adapterMyOrders = new AdapterMyOrders(mContext,null);
                        binding.rvMyOrder.setAdapter(adapterMyOrders);
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

    private void supportApi() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.getSupportApi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("supportApi","supportApi = " + stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelSupport modelSupport = new Gson().fromJson(stringResponse,ModelSupport.class);
                        supportDialog(modelSupport);
                    } else {
                        Toast.makeText(SelectService.this, "Support is not available at this time", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });
    }

    private void supportDialog(ModelSupport modelSupport) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SupportDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.support_dialog,
                        null,false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterSupports adapterSupports = new AdapterSupports(mContext,modelSupport.getResult());
        dialogBinding.rvSupport.setAdapter(adapterSupports);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void compAccDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        CompAccountDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.comp_account_dialog,
                        null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, LoginActivity.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}