package com.tech.amanah.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.LatLngInterpolator;
import com.tech.amanah.Utils.MarkerAnimation;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.retrofitutils.Api;
import com.tech.amanah.Utils.retrofitutils.ApiFactory;
import com.tech.amanah.databinding.ActivityTrackBinding;
import com.tech.amanah.databinding.ActivityTrackDriverBinding;
import com.tech.amanah.shops.activities.ShopHomeAct;
import com.tech.amanah.taxiservices.models.ModelLogin;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackDriverAct extends AppCompatActivity
        implements OnMapReadyCallback {

    Context mContext = TrackDriverAct.this;
    GoogleMap mMap;
    ActivityTrackDriverBinding binding;
    String driverId;
    private Marker currentLocationMarker;
    Timer timer = new Timer();
    private Circle currentCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_driver);
        driverId = getIntent().getStringExtra("driver_id");
        itit();
    }

    private void itit() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackDriverAct.this);

        getLatLonApiCall();

    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.dev_marker)));
                animateCamera(currentLocation);
                // createCircle(currentLocationMarker,100.0);
            } else {
                Log.e("sdfdsfdsfds","Hello Marker Anuimation");
                animateCamera(currentLocation);
                // createCircle(currentLocationMarker,100.0);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker,currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timer != null) {
            timer.cancel();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() { getLatLonApiCall(); }
            },0,4000);
        } else {
            timer = new Timer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(timer != null) {
            timer.cancel();
        }
    }

    private void getLatLonApiCall() {

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("user_id",driverId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getDriverLatLonCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        double lat = Double.parseDouble(jsonObject.getString("lat"));
                        double lon = Double.parseDouble(jsonObject.getString("lon"));
                        LatLng latLng = new LatLng(lat,lon);
                        showMarkerCurrentLocation(latLng);
                    } else {}
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

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(17).build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void createCircle(Marker currentMarker ,Double radius) {
        if(currentCircle!=null){
            currentCircle.remove();
        }
        currentCircle=mMap.addCircle(new CircleOptions().center(currentMarker.getPosition()).radius(radius)
                .strokeColor(Color.parseColor("#FF007A93"))
                .fillColor(Color.parseColor("#40007A93"))
                .strokeWidth(2));
    }

}