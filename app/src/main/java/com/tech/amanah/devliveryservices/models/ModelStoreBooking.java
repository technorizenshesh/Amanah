package com.tech.amanah.devliveryservices.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelStoreBooking implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    private String total_amount;

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
    public void setTotal_amount(String total_amount){
        this.total_amount = total_amount;
    }
    public String getTotal_amount(){
        return this.total_amount;
    }

    public class Result implements Serializable
    {
        private String id;

        private String user_id;

        private String order_id;

        private String shop_id;

        private String address;

        private String payment_type;

        private String driver_name;
        private String name;
        private String driver_mobile;
        private String driver_lisence_img;
        private String responsible_letter_img;
        private String identification_img;

        private String status;

        private String date_time;

        private String amount;

        private String cart_id;

        private String book_date;

        private String qr_image;

        private String lat;

        private String lon;

        private String book_time;

        private String customer_holder_name;

        private String payment_status;

        private String driver_id;

        private String shop_name;

        private String shop_address;

        private String shop_front_image;

        private String id_card_image;

        private String business_license_image;

        private ArrayList<Cart_list> cart_list;

        public String getQr_image() {
            return qr_image;
        }

        public void setQr_image(String qr_image) {
            this.qr_image = qr_image;
        }

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriver_mobile() {
            return driver_mobile;
        }

        public void setDriver_mobile(String driver_mobile) {
            this.driver_mobile = driver_mobile;
        }

        public String getDriver_lisence_img() {
            return driver_lisence_img;
        }

        public void setDriver_lisence_img(String driver_lisence_img) {
            this.driver_lisence_img = driver_lisence_img;
        }

        public String getResponsible_letter_img() {
            return responsible_letter_img;
        }

        public void setResponsible_letter_img(String responsible_letter_img) {
            this.responsible_letter_img = responsible_letter_img;
        }

        public String getIdentification_img() {
            return identification_img;
        }

        public void setIdentification_img(String identification_img) {
            this.identification_img = identification_img;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setShop_id(String shop_id){
            this.shop_id = shop_id;
        }
        public String getShop_id(){
            return this.shop_id;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setPayment_type(String payment_type){
            this.payment_type = payment_type;
        }
        public String getPayment_type(){
            return this.payment_type;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
        public void setCart_id(String cart_id){
            this.cart_id = cart_id;
        }
        public String getCart_id(){
            return this.cart_id;
        }
        public void setBook_date(String book_date){
            this.book_date = book_date;
        }
        public String getBook_date(){
            return this.book_date;
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
        public void setBook_time(String book_time){
            this.book_time = book_time;
        }
        public String getBook_time(){
            return this.book_time;
        }
        public void setCustomer_holder_name(String customer_holder_name){
            this.customer_holder_name = customer_holder_name;
        }
        public String getCustomer_holder_name(){
            return this.customer_holder_name;
        }
        public void setPayment_status(String payment_status){
            this.payment_status = payment_status;
        }
        public String getPayment_status(){
            return this.payment_status;
        }
        public void setShop_name(String shop_name){
            this.shop_name = shop_name;
        }
        public String getShop_name(){
            return this.shop_name;
        }
        public void setShop_address(String shop_address){
            this.shop_address = shop_address;
        }
        public String getShop_address(){
            return this.shop_address;
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
        public void setCart_list(ArrayList<Cart_list> cart_list){
            this.cart_list = cart_list;
        }
        public ArrayList<Cart_list> getCart_list(){
            return this.cart_list;
        }

        public class Cart_list implements Serializable
        {
            private String id;

            private String user_id;

            private String shop_id;

            private String quantity;

            private String date_time;

            private String item_id;

            private String status;

            private String item_name;

            private String item_price;

            private String item_image;

            public String getItem_name() {
                return item_name;
            }

            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }

            public String getItem_price() {
                return item_price;
            }

            public void setItem_price(String item_price) {
                this.item_price = item_price;
            }

            public String getItem_image() {
                return item_image;
            }

            public void setItem_image(String item_image) {
                this.item_image = item_image;
            }

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setUser_id(String user_id){
                this.user_id = user_id;
            }
            public String getUser_id(){
                return this.user_id;
            }
            public void setShop_id(String shop_id){
                this.shop_id = shop_id;
            }
            public String getShop_id(){
                return this.shop_id;
            }
            public void setQuantity(String quantity){
                this.quantity = quantity;
            }
            public String getQuantity(){
                return this.quantity;
            }
            public void setDate_time(String date_time){
                this.date_time = date_time;
            }
            public String getDate_time(){
                return this.date_time;
            }
            public void setItem_id(String item_id){
                this.item_id = item_id;
            }
            public String getItem_id(){
                return this.item_id;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
        }

    }

}
