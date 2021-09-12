package com.tech.amanah.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.InternetConnection;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityLoginBinding;
import com.tech.amanah.shops.activities.ShopHomeAct;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    Context mContext = LoginActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);

        allOnclickListeners();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(
                task -> Log.e("tokentoken", "This is the token : " + task.getResult()
        ));

    }

    private void allOnclickListeners() {

        binding.rlLinkSignup.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(i);
        });

        binding.rlForgotPass.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
            LoginActivity.this.startActivity(i);
        });

        binding.btnSignin.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else {
                if(InternetConnection.checkConnection(mContext)) {
                    loginApiCall();
                } else {
                    MyApplication.showConnectionDialog(mContext);
                }
            }
        });

    }

    private void loginApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("email",binding.etEmail.getText().toString().trim());
        paramHash.put("password",binding.etPass.getText().toString().trim());
        paramHash.put("lat","");
        paramHash.put("lon","");
        paramHash.put("register_id",registerId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.loginApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        if(modelLogin.getResult().getType().equals(AppConstant.USER)) {
                            startActivity(new Intent(mContext,SelectService.class));
                            finish();
                        } else {
                            startActivity(new Intent(mContext, ShopHomeAct.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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