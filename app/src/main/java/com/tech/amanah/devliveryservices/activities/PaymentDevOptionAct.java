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
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityPaymentDevOptionBinding;
import com.tech.amanah.databinding.ActivityPaymentOptionBinding;
import com.tech.amanah.devliveryservices.adapters.AdapterShopType;
import com.tech.amanah.devliveryservices.models.ModelShopCat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDevOptionAct extends AppCompatActivity {

    Context mContext = PaymentDevOptionAct.this;
    ActivityPaymentDevOptionBinding binding;
    String paymentType = "";
    private HashMap<String,String> param = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_dev_option);
        param = (HashMap<String, String>) getIntent().getSerializableExtra("param");
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rbHelloCash.setOnClickListener(v -> {
            binding.rbSahay.setChecked(false);
            binding.rbEbirr.setChecked(false);
        });

        binding.rbSahay.setOnClickListener(v -> {
            binding.rbEbirr.setChecked(false);
            binding.rbHelloCash.setChecked(false);
        });

        binding.rbEbirr.setOnClickListener(v -> {
            binding.rbHelloCash.setChecked(false);
            binding.rbSahay.setChecked(false);
        });

        binding.btnNext.setOnClickListener(v -> {
            if(binding.rbEbirr.isChecked()) {
                paymentType = AppConstant.EBIRR;
                param.put("payment_type",paymentType);
                startActivity(new Intent(mContext,CompanyDetailsAct.class)
                        .putExtra("paytype",paymentType)
                        .putExtra("param",param)
                );
            } else if(binding.rbSahay.isChecked()) {
                paymentType = AppConstant.SAHAY;
                param.put("payment_type",paymentType);
                startActivity(new Intent(mContext,CompanyDetailsAct.class)
                        .putExtra("paytype",paymentType)
                        .putExtra("param",param)
                );
            } else if (binding.rbHelloCash.isChecked()) {
                paymentType = AppConstant.HELLO_CASH;
                param.put("payment_type",paymentType);
                startActivity(new Intent(mContext,CompanyDetailsAct.class)
                        .putExtra("paytype",paymentType)
                        .putExtra("param",param)
                );
            } else {
                Toast.makeText(mContext, getString(R.string.please_pay_option), Toast.LENGTH_SHORT).show();
            }
        });

    }


}