package com.tech.amanah.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
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

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

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

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int hours = mins / 60;
            mins = mins % 60;
            // int milliseconds = (int) (updatedTime % 1000);
            // + ":" + String.format("%03d", milliseconds)
            String timer = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            binding.etEmail.setText(timer);
            Log.e("zafdsfasdfsd","Last Timer = " + timer);
            // set yout textview to the String timer here
            customHandler.postDelayed(this, 1000);

        }

    };

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void stopTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    private void allOnclickListeners() {

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.rlLinkSignup.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this,SignupActivity.class);
            startActivity(i);
        });

        binding.rlForgotPass.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
            LoginActivity.this.startActivity(i);
        });

//        binding.tvLogin.setOnClickListener(v -> {
//            stopTimer();
//        });

        binding.btnSignin.setOnClickListener(v -> {
            // startTimer();
            if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPass.getText().toString().trim())) {
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

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioSpanish = dialog.findViewById(R.id.radioSpanish);

        if ("so".equals(sharedPref.getLanguage("lan"))) {
            radioSpanish.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finish();
                startActivity(new Intent(mContext, LoginActivity.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "so");
                sharedPref.setlanguage("lan", "so");
                finish();
                startActivity(new Intent(mContext, LoginActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();

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