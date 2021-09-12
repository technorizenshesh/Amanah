package com.tech.amanah.devliveryservices.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityCompanyDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyDetailsAct extends AppCompatActivity {

    Context mContext = CompanyDetailsAct.this;
    ActivityCompanyDetailsBinding binding;
    private HashMap<String,String> param = new HashMap<>();
    String finalAccountNumber;
    String payType,EbirrAccNo="826335",SahayAccNo="760627",HelloCashAccNo="2537406";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_company_details);
        param = (HashMap<String,String>) getIntent().getSerializableExtra("param");
        payType = getIntent().getStringExtra("paytype");
        itit();
    }

    private void itit() {

        binding.tvPayAmount.setText(param.get("amounttotal"));
        binding.tvPayType.setText(payType);

        if(AppConstant.EBIRR.equals(payType)) {
            finalAccountNumber = EbirrAccNo;
            binding.etComapnyAccount.setText(finalAccountNumber);
        } else if(AppConstant.SAHAY.equals(payType)) {
            finalAccountNumber = SahayAccNo;
            binding.etComapnyAccount.setText(finalAccountNumber);
        } else if(AppConstant.HELLO_CASH.equals(payType)) {
            finalAccountNumber = HelloCashAccNo;
            binding.etComapnyAccount.setText(finalAccountNumber);
        }

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnNext.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etHolderName.getText().toString())) {
                Toast.makeText(mContext, getString(R.string.enter_accounthol_name), Toast.LENGTH_LONG).show();
            } else {
                placeOrderApi();
            }
        });

    }

    private void placeOrderApi() {

        param.put("customer_holder_name",binding.etHolderName.getText().toString());
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        param.remove("amounttotal");
        Log.e("adfasdasdasd","param = " + param.toString());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.placeDevOrderApiCall(param);
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
                            Log.e("asfddasfasdf","stringResponse = " + stringResponse);
                            orderDialog();
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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

    private void orderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.order_place_success_text)
                +"\nYour selected Payment Option is : "+payType+"\nThis is Company Account Number : \n"
                +finalAccountNumber+"\nNow move forward by clicking ok");
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", finalAccountNumber, null));
                startActivity(intent);
            }
        }).create().show();
    }

}