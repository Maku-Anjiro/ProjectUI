package com.example.uidesign.network.endpoints;

import com.example.uidesign.network.models.AllVisitors;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface GetAllVisitors {

    @Headers({"Content-Type: application/json"})
    @GET("visitors")
    Call<AllVisitors> getAllVisitors();
}
