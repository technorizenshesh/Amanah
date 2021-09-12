package com.tech.amanah.taxiservices.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.Constent.DrawPollyLine;
import com.tech.amanah.R;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.activities.LoginActivity;
import com.tech.amanah.activities.ProfileActivity;
import com.tech.amanah.databinding.ActivityTaxiappHomeBinding;
import com.tech.amanah.fragments.BottomSheetFragment;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.Dialogs.DialogSearchingDriver;
import com.tech.amanah.taxiservices.Interfaces.onSearchingDialogListener;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelAvailableDriver;
import com.tech.amanah.taxiservices.models.ModelCurrentBookingResult;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.GPSTracker;
import com.tech.amanah.utility.NetworkAvailablity;
import com.tech.amanah.utility.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class TaxiHomeAct extends AppCompatActivity implements OnMapReadyCallback,
        onSearchingDialogListener {

    private GoogleMap mMap;
    ActivityTaxiappHomeBinding binding;
    GPSTracker gpsTracker;
    Context mContext = TaxiHomeAct.this;
    int PERMISSION_ID = 44;
    private SessionManager session;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
    private PlacesClient placesClient;
    private static final String TAG = "HomeAct";
    private LatLng PickUpLatLng,DropOffLatLng;
    private MarkerOptions PicUpMarker,DropOffMarker;
    private boolean isAddedMarker2=false,isAddedMarker1=false;
    private PolylineOptions lineOptions;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private ScheduledExecutorService scheduleTaskExecutor;
    public static String pickUpAddress = null,dropOffAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_home);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxiapp_home);
        session = SessionManager.get(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // BindData();
        initViews();

    }

    private void BindData() {

        gpsTracker = new GPSTracker(TaxiHomeAct.this);

        if(pickUpAddress == null) {
            PickUpLatLng = new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
            binding.tvFrom.setText(Tools.getCompleteAddressString(this,gpsTracker.getLatitude(),gpsTracker.getLongitude()));
        }

        session = SessionManager.get(this);

        Log.e("UserDetails","==>" + session.getAllDetails());

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        Picasso.get().load(session.getValue().getImage()).placeholder(R.drawable.user_ic)
                .into(binding.childNavDrawer.ivProfileImage);

    }

    private void initViews() {

        placesClient = Places.createClient(this);

        PicUpMarker=new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker=new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.ivMenu.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            // startActivity(new Intent(this, RideHistoryActivity.class));
        });

//        binding.childNavDrawer.btnPayment.setOnClickListener(v -> {
//            startActivity(new Intent(this, CardAdded.class));
//        });
//
//        binding.childNavDrawer.btnPromocode.setOnClickListener(v -> {
//            startActivity(new Intent(this, PromoCodeActivity.class));
//        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            // startActivity(new Intent(this, SupportActivity.class));
        });

//        binding.childNavDrawer.btnWallet.setOnClickListener(v -> {
//            // startActivity(new Intent(this, WalletAct.class));
//        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            session.Logout();
            sharedPref.clearAllPreferences();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.btnNext.setOnClickListener(v -> {

//            if (PickUpLatLng == null) {
//                Toast.makeText(this, "Please select Pickup Location", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            if (DropOffLatLng == null) {
//                Toast.makeText(this, "Please select Dropoff Location", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Intent intent = new Intent(this, RideOptionActivity.class);
//            intent.putExtra("pollyLine",lineOptions);
//            intent.putExtra("PickUp",PickUpLatLng);
//            intent.putExtra("DropOff",DropOffLatLng);
//            startActivity(intent);

            Intent intent = new Intent(this, RideOptionActivity.class);
//            intent.putExtra("pollyLine",lineOptions);
//            intent.putExtra("PickUp",PickUpLatLng);
//            intent.putExtra("DropOff",DropOffLatLng);
            startActivity(intent);

        });

        binding.tvFrom.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1002);
        });

        binding.tvDestination.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1003);
        });

    }

    public void navmenu() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            binding.drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                PicUpMarker.position(new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
//                if (!isAddedMarker1) {
//                    mMap.addMarker(PicUpMarker);
//                    isAddedMarker1 = true;
//                }
//            } else {
//                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        } else {
//            requestPermissions();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                PickUpLatLng = place.getLatLng();
                binding.tvFrom.setText(Tools.getCompleteAddressString(mContext,PickUpLatLng.latitude,PickUpLatLng.longitude));
                if (PickUpLatLng!=null) {
                    PicUpMarker.position(PickUpLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1=true;
                    }
                }
                if (PickUpLatLng!=null&DropOffLatLng!=null) {
                    DrawPolyLine();
                }
            }
        } else if(requestCode == 1003) {
//            if (resultCode == RESULT_OK) {
//                Place place = Autocomplete.getPlaceFromIntent(data);
//                DropOffLatLng = place.getLatLng();
//                binding.tvDestination.setText(Tools.getCompleteAddressString(mContext,DropOffLatLng.latitude,DropOffLatLng.longitude));
//                if (DropOffLatLng!=null) {
//                    PicUpMarker.position(DropOffLatLng);
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(DropOffLatLng)));
//                    if (!isAddedMarker1) {
//                        mMap.addMarker(PicUpMarker);
//                        isAddedMarker1=true;
//                    }
//                }
//                if (PickUpLatLng!=null&DropOffLatLng!=null) {
//                    DrawPolyLine();
//                }
//            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (session.getUserID().isEmpty()) {
//            DialogMessage.get(this).setMessage(getString(R.string.session_expire)).Callback(()->{
//                session.Logout();
//                startActivity(new Intent(this,LoginActivity.class));
//                finish();
//            }).show();
//        }
//        getProfile();
//        BindExecutor();
//        getNearDriver();
//        getCurrentBooking();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scheduleTaskExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // scheduleTaskExecutor.shutdownNow();
    }

    private void BindExecutor() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getNearDriver();
            }
        }, 0, 8, TimeUnit.MINUTES);
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        if (latLng==null) {
            latLng=new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude());
        }
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PicUpMarker.position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
                if (!isAddedMarker1) {
                    isAddedMarker1 = true;
                    mMap.addMarker(PicUpMarker);
                }
            }
        }
    }

    private void getProfile(){
        HashMap<String,String> param=new HashMap<>();
        param.put("user_id",session.getUserID());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getProfile())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("1")){
                        session.CreateSession(object.getString("result"));
                        BindData();
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

    private void FindLatLng(int type,String placeId) {
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i(TAG, "Place found: " + place.getName());
            if (type==1){
                binding.tvFrom.setText(place.getAddress());
                PickUpLatLng=place.getLatLng();
                if (PickUpLatLng!=null) {
                    PicUpMarker.position(PickUpLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1=true;
                    }
                }
            }
            if (type==2) {
                binding.tvDestination.setText(place.getAddress());
                DropOffLatLng=place.getLatLng();
                if (DropOffLatLng!=null){
                    DropOffMarker.position(DropOffLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(DropOffLatLng)));
                    if (!isAddedMarker2) {
                        isAddedMarker2=true;
                        mMap.addMarker(DropOffMarker);
                    }
                }
            }
            if (PickUpLatLng!=null&DropOffLatLng!=null) {
                DrawPolyLine();
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        mMap.clear();
                        lineOptions = new PolylineOptions();
                        lineOptions.addAll(latLngs);
                        lineOptions.width(10);
                        lineOptions.color(Color.BLUE);
                        AddDefaultMarker();
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

    public void AddDefaultMarker() {
        if (mMap!=null) {
            mMap.clear();
            if (lineOptions!=null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng!=null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng!=null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
        zoomMapInitial(PickUpLatLng,DropOffLatLng);
    }

    private void getNearDriver() {

        if (PickUpLatLng==null) {
            return;
        }
        HashMap<String,String>param=new HashMap<>();
        param.put("latitude","" + PickUpLatLng.latitude);
        param.put("longitude","" + PickUpLatLng.longitude);
        param.put("user_id",session.getUserID());
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getNearAllDriver())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                Log.e("NearBy",response);
                try {
                    JSONObject object=new JSONObject(response);
                    if (object.getString("status").equals("1")){
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {}.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(object.getString("result"), listType);
                        if (mMap!=null) {
                            AddDefaultMarker();
                            for (ModelAvailableDriver driver : drivers) {
                                MarkerOptions car=new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()),Double.valueOf(driver.getLon())))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
                                mMap.addMarker(car);
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

    private void getCurrentBooking(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id", session.getUserID());
        param.put("type", "USER");
        param.put("timezone", Tools.get().getTimeZone());
        ApiCallBuilder.build(this).setUrl(BaseClass.get().getCurrentBooking())
                .setParam(param).isShowProgressBar(true)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            if (object.getString("status").equals("1")){
                                Type listType = new TypeToken<ModelCurrentBooking>(){}.getType();
                                ModelCurrentBooking data = new GsonBuilder().create().fromJson(response, listType);
                                if (data.getStatus().equals(1)) {
                                    ModelCurrentBookingResult result=data.getResult().get(0);
                                    if (result.getStatus().equalsIgnoreCase("Pending")) {
                                        DialogSearchingDriver.get(TaxiHomeAct.this).Callback(TaxiHomeAct.this).show();
                                    }else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                        Intent k = new Intent(TaxiHomeAct.this, TrackActivity.class);
                                        k.putExtra("data",data);
                                        startActivity(k);
                                    } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                        Intent j = new Intent(TaxiHomeAct.this, TrackActivity.class);
                                        j.putExtra("data",data);
                                        startActivity(j);
                                    } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                        Intent j = new Intent(TaxiHomeAct.this, TrackActivity.class);
                                        j.putExtra("data",data);
                                        startActivity(j);
                                    } else if (result.getStatus().equalsIgnoreCase("End")) {
                                        Intent j = new Intent(TaxiHomeAct.this, PaymentSummary.class);
                                        j.putExtra("data",data);
                                        startActivity(j);
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

    @Override
    public void onRequestAccepted(ModelCurrentBooking data) {
        DialogMessage.get(this).setMessage("Your request accepted successfully!").Callback(()->{

        }).show();
    }

    @Override
    public void onRequestCancel() {
        DialogMessage.get(this).setMessage("Your request has been canceled.").Callback(()->finish()).show();
    }

    @Override
    public void onDriverNotFound() {
        DialogMessage.get(this).setMessage("No driver available near you!").show();
    }
}