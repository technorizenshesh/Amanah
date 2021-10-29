package com.tech.amanah.devliveryservices.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelMyStoreCart implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;
    private int total_amount;
    private String delivery_charge;

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public void setResult(ArrayList<Result> result) {
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
    public void setTotal_amount(int total_amount){
        this.total_amount = total_amount;
    }
    public int getTotal_amount(){
        return this.total_amount;
    }

    public class Result implements Serializable {

        private String id;

        private String item_name;

        private String item_price;

        private String item_description;

        private String item_image;

        private String date_time;

        private String user_id;

        private String quantity;

        private String item_id;

        private String item_amount;

        private String cart_id;

        private String shop_id;

        private String image;

        public String getItem_amount() {
            return item_amount;
        }

        public void setItem_amount(String item_amount) {
            this.item_amount = item_amount;
        }

        public String getShop_id() {
            return shop_id;
        }

        public void setShop_id(String shop_id) {
            this.shop_id = shop_id;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
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
        public void setQuantity(String quantity){
            this.quantity = quantity;
        }
        public String getQuantity(){
            return this.quantity;
        }
        public void setItem_id(String item_id){
            this.item_id = item_id;
        }
        public String getItem_id(){
            return this.item_id;
        }
        public void setCart_id(String cart_id){
            this.cart_id = cart_id;
        }
        public String getCart_id(){
            return this.cart_id;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
    }


}
