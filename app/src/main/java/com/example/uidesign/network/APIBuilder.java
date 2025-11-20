package com.example.uidesign.network;

import android.content.Context;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIBuilder {
    private static final String BASE_URL = "http://192.168.7.51:9898/qrgate/";



    private Retrofit retrofit;
    private Retrofit retrofitQRCode;

    public APIBuilder(Context context) {
        Gson gson = new GsonBuilder()
                .setLenient()   // THIS FIXES "malformed JSON" error
                .create();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitQRCode = new Retrofit.Builder()
                .baseUrl("https://api.qrserver.com/v1/")
                .addConverterFactory(GsonConverterFactory.create()) // or none for raw bytes
                .build();

    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
    public Retrofit getRetrofitForQRCode(){
    return retrofitQRCode;
    }
}
