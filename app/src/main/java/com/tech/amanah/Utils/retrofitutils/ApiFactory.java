package com.tech.amanah.Utils.retrofitutils;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.tech.amanah.Utils.AppConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    public static String BASE_URL = AppConstant.BASE_URL;
    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithHeader = null;
    private static SharedPreferences preferences;
    private static String access_token = "";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Retrofit getClientWithoutHeader(Context context) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = httpClient.addInterceptor(interceptor).connectTimeout(5, TimeUnit.MINUTES).
                readTimeout(120, TimeUnit.SECONDS).
                writeTimeout(120, TimeUnit.SECONDS)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
