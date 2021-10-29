package com.tech.amanah.taxiservices.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tech.amanah.Application.MyApplication;
import com.tech.amanah.Constent.BaseClass;
import com.tech.amanah.R;
import com.tech.amanah.Utils.AppConstant;
import com.tech.amanah.Utils.SessionManager;
import com.tech.amanah.Utils.SharedPref;
import com.tech.amanah.adapters.CarAdapter;
import com.tech.amanah.databinding.ActivityRideOptionBinding;
import com.tech.amanah.taxiservices.Dialogs.DialogMessage;
import com.tech.amanah.taxiservices.Dialogs.DialogSearchingDriver;
import com.tech.amanah.taxiservices.Interfaces.onSearchingDialogListener;
import com.tech.amanah.taxiservices.ModelCurrentBooking;
import com.tech.amanah.taxiservices.models.ModelAvailableDriver;
import com.tech.amanah.taxiservices.models.ModelCar;
import com.tech.amanah.taxiservices.models.ModelLogin;
import com.tech.amanah.utility.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import www.develpoeramit.mapicall.ApiCallBuilder;
import www.develpoeramit.mapicall.Method;

public class RideOptionActivity extends AppCompatActivity implements
        OnMapReadyCallback, onSearchingDialogListener {

    ActivityRideOptionBinding binding;
    GoogleMap mMap;
    public static Context context;
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng;
    private MarkerOptions PicUpMarker, DropOffMarker;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String CarTypeID = "", amount  ="";
    String paymentType = "";
    Context mContext = RideOptionActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = RideOptionActivity.this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_option);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getData();
        initView();
    }

    private void getData() {

        if (getIntent().getExtras() != null) {
            lineOptions = (PolylineOptions) getIntent().getExtras().get("pollyLine");
            PickUpLatLng = (LatLng) getIntent().getExtras().get("PickUp");
            DropOffLatLng = (LatLng) getIntent().getExtras().get("DropOff");
        }

    }

    private void initView() {

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        getCar();

        // MyApplication.get().update(this::getCar);

        binding.btnBookRide.setOnClickListener(v -> {
            if (Validation()) {
                BookingRequest();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        AddDefaultMarker();
    }

    private void getCar() {

        HashMap<String, String> param = new HashMap<>();
        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);

        if (DropOffLatLng != null) {
            param.put("droplat", "" + DropOffLatLng.latitude);
            param.put("droplon", "" + DropOffLatLng.longitude);
        } else {
            param.put("droplat", "");
            param.put("droplon", "");
        }

        param.put("user_id", modelLogin.getResult().getId());

        Log.e("getCargetCar", "getCar = " + param);

        ApiCallBuilder.build(this)
                .setUrl(BaseClass.get().getCarList())
                .setParam(param)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("GetCar", "==>" + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                Type listType = new TypeToken<ArrayList<ModelCar>>() {
                                }.getType();
                                ArrayList<ModelCar> cars = new GsonBuilder().create().fromJson(object.getString("result"), listType);
                                cars.get(0).setSelected(true);
                                binding.recycleView.setAdapter(new CarAdapter(context, cars).Callback(RideOptionActivity.this::onSelectCar));
                                onSelectCar(cars.get(0));
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

    private void onSelectCar(ModelCar car) {
        Log.e("fsadasdas","Car Total = " + car.getTotal());

        amount = car.getTotal();
        CarTypeID = car.getId();
        getNearDriver(car.getId());
        // binding.tvRideDistance.setText(car.getDistance());
    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng != null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
    }

    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void getNearDriver(String id) {
        HashMap<String, String> param = new HashMap<>();
        param.put("latitude", "" + PickUpLatLng.latitude);
        param.put("longitude", "" + PickUpLatLng.longitude);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", Tools.get().getTimeZone());
        param.put("car_type_id", id);

        Log.e("getNearDriver", "getNearDriver = " + param);

        ApiCallBuilder.build(this)
                .setUrl(BaseClass.get().getNearDriver())
                .setParam(param).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status").equals("1")) {
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {
                        }.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(object.getString("result"), listType);
                        if (mMap != null) {
                            AddDefaultMarker();
                            for (ModelAvailableDriver driver : drivers) {
                                MarkerOptions car = new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon())))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)).title(driver.getName());
                                mMap.addMarker(car);
                            }
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

    private HashMap<String, String> getBookingParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("car_type_id", CarTypeID);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("picuplocation", Tools.getCompleteAddressString(this, PickUpLatLng.latitude, PickUpLatLng.longitude));

        if (DropOffLatLng != null) {
            param.put("dropofflocation", Tools.getCompleteAddressString(this, DropOffLatLng.latitude, DropOffLatLng.longitude));
            param.put("droplat", "" + DropOffLatLng.latitude);
            param.put("droplon", "" + DropOffLatLng.longitude);
        } else {
            param.put("dropofflocation", "");
            param.put("droplat", "");
            param.put("droplon", "");
        }

        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);
        param.put("shareride_type", paymentType);
        param.put("booktype", "Now");
        param.put("status", "Now");
        param.put("passenger", "1");
        param.put("current_time", "" + Tools.getCurrentDateTime());
        param.put("timezone", "" + Tools.get().getTimeZone());
        param.put("apply_code", "");
        param.put("vehical_type", "Reqular");
        param.put("picklatertime", "");
        param.put("picklaterdate", "");
        param.put("route_img", "");
        param.put("amount", amount);

        Log.e("param", param.toString().replace(", ", "&"));

        return param;
    }

    private boolean Validation() {
        if (CarTypeID.equals("")) {
            Toast.makeText(context, getString(R.string.select_car_type), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.rbEbirr.isChecked()) {
            paymentType = getString(R.string.ebirr);
            return true;
        } else if (binding.rbSahay.isChecked()) {
            paymentType = getString(R.string.sahay);
            return true;
        } else if (binding.rbHelloCash.isChecked()) {
            paymentType = getString(R.string.hello_cash);
            return true;
        } else if (binding.rbCash.isChecked()) {
            paymentType = getString(R.string.cash);
            return true;
        } else {
            Toast.makeText(context, getString(R.string.select_pay_type), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void BookingRequest() {
        ApiCallBuilder.build(this)
                .setMethod(Method.POST)
                .setUrl(BaseClass.get().bookingRequest())
                .setParam(getBookingParam())
                .setFile("route_img", "")
                .isShowProgressBar(true)
                .execute(new ApiCallBuilder.onResponse() {
                    @Override
                    public void Success(String response) {
                        Log.e("BookingRequest", "=====>" + response);
                        Log.e("BookingRequest", "=====>" + getBookingParam());
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("1")) {
                                if (!object.getString("message").equals("already in ride")) {
                                    String request_id = object.getString("request_id");
                                    sharedPref.setlanguage(AppConstant.LAST, request_id);
                                }
                                DialogSearchingDriver.get(context).Callback(RideOptionActivity.this).show();
                            } else {
                                onDriverNotFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void Failed(String error) {
                        Log.e("Error", error);
                    }

                });
    }

    @Override
    public void onRequestAccepted(ModelCurrentBooking data) {
        Intent k = new Intent(RideOptionActivity.this, TrackActivity.class);
        k.putExtra("data", data);
        startActivity(k);
        finish();
//      DialogMessage.get(this).setMessage("Your request accepted successfully!")
//              .Callback(() -> {
//              }).show();
    }

    @Override
    public void onRequestCancel() {
        DialogMessage.get(this).setMessage("Your request has been canceled.").Callback(() -> finish()).show();
    }

    @Override
    public void onDriverNotFound() {
        DialogMessage.get(this).setMessage("No driver available near you!").show();
    }

}