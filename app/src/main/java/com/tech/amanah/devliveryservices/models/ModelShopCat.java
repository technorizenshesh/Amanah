package com.tech.amanah.devliveryservices.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelShopCat implements Serializable {

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

    public class Result
    {
        private String id;

        private String category_name;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setCategory_name(String category_name){
            this.category_name = category_name;
        }
        public String getCategory_name(){
            return this.category_name;
        }
    }

    
}
