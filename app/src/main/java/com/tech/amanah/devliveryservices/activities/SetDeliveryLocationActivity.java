package com.tech.amanah.devliveryservices.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.activities.PinLocationActivity;
import com.tech.amanah.databinding.ActivitySetDeliveryLocationBinding;
import com.tech.amanah.taxiservices.models.ModelLogin;

import java.util.HashMap;

public class SetDeliveryLocationActivity extends AppCompatActivity {

    Context mContext = SetDeliveryLocationActivity.this;
    ActivitySetDeliveryLocationBinding binding;
    private LatLng latLng;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    AlertDialog.Builder alertBuilder;
    HashMap<String, String> bookingParams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_delivery_location);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        bookingParams = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstant.STORE_BOOKING_PARAMS);

        Log.e("fdsfdsfsdfds", bookingParams.toString());

        itit();

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.etAddress.setOnClickListener(v -> {
            startActivityForResult(new Intent(mContext, PinLocationActivity.class), 222);
        });

        binding.btNext.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etAddress.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etLandMark.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enterlandmark_add), Toast.LENGTH_SHORT).show();
            } else {
                bookingParams.put("address", binding.etAddress.getText().toString().trim()
                        + " " + binding.etLandMark.getText().toString().trim());
                bookingParams.put("lat", String.valueOf(latLng.latitude));
                bookingParams.put("lon", String.valueOf(latLng.longitude));

                startActivity(new Intent(mContext, PaymentDevOptionAct.class)
                        .putExtra("param", bookingParams)
                );

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 222) {
            String add = data.getStringExtra("add");
            Log.e("sfasfdas", "fdasfdas = 222 = " + add);
            Log.e("sfasfdas", "fdasfdas = lat = " + data.getDoubleExtra("lat", 0.0));
            Log.e("sfasfdas", "fdasfdas = lon = " + data.getDoubleExtra("lon", 0.0));
            double lat = data.getDoubleExtra("lat", 0.0);
            double lon = data.getDoubleExtra("lon", 0.0);
            latLng = new LatLng(lat, lon);
            binding.etAddress.setText(add);
        }
    }

}