package com.tech.amanah.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityChnagePassBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChnagePassAct extends AppCompatActivity {

    Context mContext = ChnagePassAct.this;
    ActivityChnagePassBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chnage_pass);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etOldPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.old_pass_text), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etNewPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.new_pass_text), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etConfirmPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.confirm_pass_text), Toast.LENGTH_SHORT).show();
            } else if(!binding.etNewPass.getText().toString().trim().equals(binding.etConfirmPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.pass_not_match), Toast.LENGTH_SHORT).show();
            } else {
                changePassword();
            }
        });

    }

    private void changePassword() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("old_pass",binding.etOldPass.getText().toString().trim());
        param.put("new_pass",binding.etNewPass.getText().toString().trim());

        Call<ResponseBody> call = api.changePass(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(mContext,SelectService.class));
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
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

}