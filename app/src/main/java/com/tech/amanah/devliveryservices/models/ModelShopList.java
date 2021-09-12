package com.tech.amanah.devliveryservices.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelShopList implements Serializable {

    private ArrayList<ModelShopList.Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<ModelShopList.Result> result){
        this.result = result;
    }
    public ArrayList<ModelShopList.Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result implements Serializable {

        private String id;

        private String shop_name;

        private String address;

        private String lat;

        private String lon;

        private String open_time;

        private String close_time;

        private String description;

        private String shop_front_image;

        private String id_card_image;

        private String business_license_image;

        private String date_time;

        private String user_id;

        private String shop_category_id;

        private String distance;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setShop_name(String shop_name){
            this.shop_name = shop_name;
        }
        public String getShop_name(){
            return this.shop_name;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setLat(String lat){
            this.lat = lat;
        }
        public String getLat(){
            return this.lat;
        }
        public void setLon(String lon){
            this.lon = lon;
        }
        public String getLon(){
            return this.lon;
        }
        public void setOpen_time(String open_time){
            this.open_time = open_time;
        }
        public String getOpen_time(){
            return this.open_time;
        }
        public void setClose_time(String close_time){
            this.close_time = close_time;
        }
        public String getClose_time(){
            return this.close_time;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public String getDescription(){
            return this.description;
        }
        public void setShop_front_image(String shop_front_image){
            this.shop_front_image = shop_front_image;
        }
        public String getShop_front_image(){
            return this.shop_front_image;
        }
        public void setId_card_image(String id_card_image){
            this.id_card_image = id_card_image;
        }
        public String getId_card_image(){
            return this.id_card_image;
        }
        public void setBusiness_license_image(String business_license_image){
            this.business_license_image = business_license_image;
        }
        public String getBusiness_license_image(){
            return this.business_license_image;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setShop_category_id(String shop_category_id){
            this.shop_category_id = shop_category_id;
        }
        public String getShop_category_id(){
            return this.shop_category_id;
        }
        public void setDistance(String distance){
            this.distance = distance;
        }
        public String getDistance(){
            return this.distance;
        }
    }

    @BindingAdapter("shopImage")
    public static void loadImage(ImageView imageView,String url){
        Picasso.get().load(url).into(imageView);
    }

}
