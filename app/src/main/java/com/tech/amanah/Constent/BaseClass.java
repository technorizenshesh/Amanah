package com.tech.amanah.Constent;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.tech.amanah.R;

public class BaseClass {

    //public static String BaseUrl="http://amanahservices.com/AmanahServices/webservice/";
    public static String BaseUrl="http://equipmeapp.co.nz/AmanahServices/webservice/";
    public static BaseClass get() {
        return new BaseClass();
    }

    public String Login(){
        return BaseUrl.concat("login");
    }
    public String SignUp(){
        return BaseUrl.concat("signup");
    }
    public String socialLogin(){
        return BaseUrl.concat("social_login");
    }
    public String getProfile(){
        return BaseUrl.concat("get_profile");
    }
    public String signupMobileVerify(){
        return BaseUrl.concat("signup_mobile_verify");
    }
    public String getPolyLineUrl(Context context, LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + context.getResources().getString( R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("PathURL","====>"+url);
        return url;
    }
    public String getNearAllDriver(){
        return BaseUrl.concat("get_available_driver");
    }
    public String getCarList(){
        return BaseUrl.concat("get_car_type_list");
    }
    public String getNearDriver(){
        return BaseUrl.concat("available_car_driver");
    }
    public String bookingRequest(){
        return BaseUrl.concat("booking_request");
    }
    public String cancelRide() {
        return BaseUrl.concat("cancel_ride");
    }
    public String getCurrentBooking() {
        return BaseUrl.concat("get_current_booking");
    }
    public String getUserHistory(){
        return BaseUrl.concat("get_user_history");
    }
    public String getDriverLatLon() {
        return BaseUrl.concat("get_lat_lon");
    }
    public String getPayment(){
        return BaseUrl.concat("get_payment");
    }
    public String addRatingReview(){
        return BaseUrl.concat("add_rating_review");
    }
    public String sendWalletAmount() {
        return BaseUrl.concat("send_wallet_amount");
    }
    public String getVerify() {
        return BaseUrl.concat("get_number_profile");
    }
    public String stripsPayment() {
        return BaseUrl.concat("strips_payment");
    }
}
