package com.tech.amanah.taxiservices.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.LatLngInterpolator;
import com.tech.amanah.Utils.MarkerAnimation;
import com.tech.amanah.Utils.ProjectUtil;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.databinding.ActivityTrackBinding;
import com.tech.amanah.databinding.RideCancellationDialogBinding;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.ModelLogin;
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

    Context mContext = TrackActivity.this;
    ActivityTrackBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private ModelLogin.Result DriverDetails;
    private LatLng PicLatLon, DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private SessionManager session;
    private Marker driverMarkerCar;
    Timer timer = null;
    String driverId = "", usermobile = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDriverLocation();
            }
        }, 0, 4000);

        if (getIntent() != null) {
            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            driverId = result.getDriverId();
            DriverDetails = result.getUserDetails().get(0);
            usermobile = DriverDetails.getMobile();
            PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));

            try {
                DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
            } catch (Exception e) {}

            binding.setDriver(DriverDetails);
            if (DriverDetails.getProfile_image() != null) {
                Glide.with(mContext).load(DriverDetails.getProfile_image())
                        .placeholder(R.drawable.user_ic).into(binding.driverImage);
            }
        }

    }

    private void rideCancelDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        RideCancellationDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.ride_cancellation_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etReason.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_reason), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                CancelRide(dialogBinding.etReason.getText().toString().trim());
            }
        });

        dialog.show();

    }

    private void CancelRide(String reason) {
        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("request_id", sharedPref.getLanguage(AppConstant.LAST));
        parmas.put("cancel_reason", reason);
        Log.e("asdasdffffffff", sharedPref.getLanguage(AppConstant.LAST));
        Log.e("asdasdffffffff", BaseClass.get().cancelRide() + "?request_id=" + sharedPref.getLanguage(AppConstant.LAST));
        ApiCallBuilder.build(TrackActivity.this)
                .setUrl(BaseClass.get().cancelRide())
                .isShowProgressBar(true)
                .setParam(parmas).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean status = object.getString("status").contains("1");
                    Toast.makeText(TrackActivity.this, "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                    if (status) {
                        finishAffinity();
                        startActivity(new Intent(TrackActivity.this, TaxiHomeAct.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {
            }

        });

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

        DropOffMarker = new MarkerOptions()
                .title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, usermobile);
        });

        binding.btnRate.setOnClickListener(v -> {
            startActivity(new Intent(TrackActivity.this, TaxiHomeAct.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });

        binding.ivCancel.setOnClickListener(v -> {
            rideCancelDialog();
//            DialogMessage.get(TrackActivity.this)
//                    .setMessage(getString(R.string.cancel_ride_text))
//                    .Callback(() -> {
//                    }).show();
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // drawRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                binding.titler.setText("Send Feedback");
                binding.btnBack.setVisibility(View.GONE);
                binding.rlDriver.setVisibility(View.GONE);
                binding.rlFeedback.setVisibility(View.VISIBLE);
            }
        }
    }

    BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("gdsfdsfdsf", "statusReceiver");
            if (intent.getAction().equals("Job_Status_Action")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    finish();
                    Toast.makeText(mContext, "Your ride has been cancelled by driver", Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentBooking();
                }
            }
        }
    };

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

        zoomMapInitial(DropLatLon, PicLatLon);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(PicLatLon));

        DrawPollyLine.get(this)
                .setOrigin(PicLatLon)
                .setDestination(DropLatLon)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
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
        registerReceiver(statusReceiver, new IntentFilter("Job_Status_Action"));

//      registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("getstatus"));
//      LocalBroadcastManager.getInstance(TrackActivity.this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));

        if (timer != null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getDriverLocation();
                }
            }, 0, 4000);
        } else {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getDriverLocation();
                }
            }, 0, 4000);
        }

        getCurrentBooking();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
        // unregisterReceiver(mRegistrationBroadcastReceiver);
        if (timer != null) timer.cancel();
//      LocalBroadcastManager.getInstance(TrackActivity.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(timer != null) timer.cancel();
//    }

    private void getDriverLocation() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", driverId);
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getDriverLatLon())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("getDriverLocation", "getDriverLocation = " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                double lat = Double.parseDouble(object.getString("lat"));
                                double lon = Double.parseDouble(object.getString("lon"));
                                Location location = new Location("");
                                location.setLatitude(lat);
                                location.setLongitude(lon);
                                location.getBearing();
                                location.getAccuracy();
                                if (driverMarkerCar == null) {
                                    DriverMarker = new MarkerOptions().title("Driver Here")
                                            .position(new LatLng(lat, lon))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
                                    driverMarkerCar = mMap.addMarker(DriverMarker);
                                    zoomCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                                } else {
                                    driverMarkerCar.setRotation(location.getBearing());
                                    Log.e("LatlonDriver = ", " driver Location = " + new LatLng(lat, lon));
                                    Log.e("LatlonDriver = ", " driver Marker = " + driverMarkerCar);
                                    MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
                                    // zoomCameraToLocation(new LatLng(location.getLatitude(),location.getLongitude()));
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                    }

                });

    }

    private void zoomCameraToLocation(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void getCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getCurrentBooking())
                .setParam(param).isShowProgressBar(false)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                Type listType = new TypeToken<ModelCurrentBooking>() {
                                }.getType();
                                ModelCurrentBooking data = new GsonBuilder().create().fromJson(response, listType);
                                if (data.getStatus().equals(1)) {
                                    ModelCurrentBookingResult result = data.getResult().get(0);
                                    if (result.getStatus().equalsIgnoreCase("Pending")) {
                                    } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                    } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                        DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.driver_arrived)).show();
                                    } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                        DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.trip_start)).show();
                                    } else if (result.getStatus().equalsIgnoreCase("End")) {
                                        DialogMessage.get(TrackActivity.this).setMessage(getString(R.string.trip_end_text))
                                                .Callback(() -> finish()).show();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                    }

                });

    }


}