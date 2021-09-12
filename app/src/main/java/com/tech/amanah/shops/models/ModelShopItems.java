package com.tech.amanah.shops.models;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelShopItems implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
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

        private String shop_id;

        private String item_name;

        private String item_price;

        private String item_description;

        private String item_image;

        private String date_time;

        private String user_id;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setShop_id(String shop_id){
            this.shop_id = shop_id;
        }
        public String getShop_id(){
            return this.shop_id;
        }
        public void setItem_name(String item_name){
            this.item_name = item_name;
        }
        public String getItem_name(){
            return this.item_name;
        }
        public void setItem_price(String item_price){
            this.item_price = item_price;
        }
        public String getItem_price(){
            return this.item_price;
        }
        public void setItem_description(String item_description){
            this.item_description = item_description;
        }
        public String getItem_description(){
            return this.item_description;
        }
        public void setItem_image(String item_image){
            this.item_image = item_image;
        }
        public String getItem_image(){
            return this.item_image;
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
    }

    @BindingAdapter("itemsImage")
    public static void loadImage(ImageView imageView,String url) {
        Picasso.get().load(url).into(imageView);
    }

}
