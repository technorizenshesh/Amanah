package com.tech.amanah.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.shops.activities.AddShopDetailsAct;
import com.tech.amanah.shops.activities.ShopHomeAct;
import com.tech.amanah.taxiservices.models.ModelLogin;

public class SplashActivity extends AppCompatActivity {

    Context mContext = SplashActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                processNextActivity();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions (
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }
    }

    private void processNextActivity() {
        Log.e("dasdasdasd","processNextActivity");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                    Log.e("dasdasdasd","shope Type = " + modelLogin.getResult().getType());
                    if("en".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext,"en");
                    } else if("so".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext,"so");
                    } else {
                        sharedPref.setlanguage("lan", "so");
                        ProjectUtil.updateResources(mContext,"so");
                    }
                    if("SHOP".equalsIgnoreCase(modelLogin.getResult().getType())) {
                        Log.e("dasdasdasd","shope status = " + modelLogin.getResult().getShop_status());
                        if("0".equals(modelLogin.getResult().getShop_status())) {
                            startActivity(new Intent(mContext, AddShopDetailsAct.class));
                            finish();
                        } else if("1".equals(modelLogin.getResult().getShop_status())) {
                            startActivity(new Intent(mContext, ShopHomeAct.class));
                            finish();
                        }
                    } else {
                        if("en".equals(sharedPref.getLanguage("lan"))) {
                            ProjectUtil.updateResources(mContext,"en");
                        } else if("so".equals(sharedPref.getLanguage("lan"))) {
                            ProjectUtil.updateResources(mContext,"so");
                        } else {
                            sharedPref.setlanguage("lan", "so");
                            ProjectUtil.updateResources(mContext,"so");
                        }
                        Intent i = new Intent(SplashActivity.this, SelectService.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    if("en".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext,"en");
                    } else if("so".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext,"so");
                    } else {
                        sharedPref.setlanguage("lan", "so");
                        ProjectUtil.updateResources(mContext,"so");
                    }
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },3000);
    }

}