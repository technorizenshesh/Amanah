package com.tech.amanah.taxiservices.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.Constent.Config;
import com.tech.amanah.Constent.DrawPollyLine;
import com.tech.amanah.R;
import com.tech.amanah.Utils.LatLngInterpolator;
import com.tech.amanah.Utils.MarkerAnimation;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.databinding.ActivityTrackBinding;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.UserDetail;
import com.tech.amanah.utility.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class TrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityTrackBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private UserDetail DriverDetails;
    private LatLng PicLatLon,DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private SessionManager session;
    private Marker driverMarkerCar;
    Timer timer = null;
    String driverId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track);

        initView();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent() != null) {
            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            driverId = result.getDriverId();
            DriverDetails = result.getUserDetails().get(0);
            PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()),Double.parseDouble(result.getPickuplon()));
            DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()),Double.parseDouble(result.getDroplon()));
            binding.setDriver(DriverDetails);
            if (DriverDetails.getImage() != null) {
                Picasso.get().load(DriverDetails.getImage()).placeholder(R.drawable.user_ic).into(binding.driverImage);
            }
        }

    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        session= SessionManager.get(this);

        DriverMarker=new MarkerOptions().title("Driver Here")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
        DropOffMarker=new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            startActivity(new Intent(TrackActivity.this,TaxiHomeAct.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        binding.ivCancel.setOnClickListener(v -> {
            DialogMessage.get(TrackActivity.this).setMessage("Are you sure you want to cancel the the ride?")
                    .Callback(()->{}).show();
//          startActivity(new Intent(TrackActivity.this,RideCancelAct.class));
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // drawRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                binding.titler.setText("Send Feedback");
                binding.btnBack.setVisibility(View.GONE);
                binding.rlDriver.setVisibility(View.GONE);
                binding.rlFeedback.setVisibility(View.VISIBLE);
            }
        }
    }

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
            } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                String message = intent.getStringExtra("message");
                JSONObject data = null;
                try {
                    data = new JSONObject(message);
                    String keyMessage = data.getString("key").trim();
                    Log.e("KEY ACCEPT REJ", "" + keyMessage);
                    getCurrentBooking();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void drawRoute() {

        DriverMarker.position(PicLatLon);
        DropOffMarker.position(DropLatLon);
        mMap.addMarker(DriverMarker);
        mMap.addMarker(DropOffMarker);

        zoomMapInitial(DropLatLon,PicLatLon);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(PicLatLon));

        DrawPollyLine.get(this)
                .setOrigin(PicLatLon)
                .setDestination(DropLatLon)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options=new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());

                        Polyline line = mMap.addPolyline(options);

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        LocalBroadcastManager.getInstance(TrackActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
//
//        if(timer != null) {
//            timer.cancel();
//        } else {
//            timer = new Timer();
//        }
//
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                getDriverLocation();
//            }
//        },0,5000);
//
//        getCurrentBooking();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(timer != null) timer.cancel();
//        LocalBroadcastManager.getInstance(TrackActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(timer != null) timer.cancel();
    }

    private void getDriverLocation() {
        HashMap<String,String> param=new HashMap<>();
        param.put("user_id", driverId);
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getDriverLatLon())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                double lat = Double.parseDouble(object.getString("lat"));
                                double lon = Double.parseDouble(object.getString("lon"));
                                Location location = new Location("");
                                location.setLatitude(lat);
                                location.setLongitude(lon);
                                driverMarkerCar.setRotation(location.getBearing());
                                Log.e("LatlonDriver = " , " driver Location = " + new LatLng(lat,lon));
                                Log.e("LatlonDriver = " , " driver Marker = " + driverMarkerCar);
                                MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(lat,lon), new LatLngInterpolator.Spherical());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {}

                });

    }

    private void getCurrentBooking() {
        HashMap<String,String> param=new HashMap<>();
        param.put("user_id", session.getUserID());
        param.put("type", "USER");
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getCurrentBooking())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                Type listType = new TypeToken<ModelCurrentBooking>(){}.getType();
                                ModelCurrentBooking data = new GsonBuilder().create().fromJson(response, listType);
                                if (data.getStatus().equals(1)) {
                                    ModelCurrentBookingResult result=data.getResult().get(0);
                                    if (result.getStatus().equalsIgnoreCase("Pending")) {
                                    } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                    } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                        DialogMessage.get(TrackActivity.this).setMessage("Driver arrived").show();
                                    } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                        DialogMessage.get(TrackActivity.this).setMessage("Trip Started").show();
                                    } else if (result.getStatus().equalsIgnoreCase("End")) {
                                        DialogMessage.get(TrackActivity.this).setMessage("Trip ended")
                                                .Callback(()->finish()).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {}

                });

    }


}