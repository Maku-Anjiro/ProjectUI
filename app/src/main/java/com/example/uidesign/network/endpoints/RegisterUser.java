package com.example.uidesign.network.endpoints;

import com.example.uidesign.network.models.RegisterModels;
import com.example.uidesign.network.response.APIResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegisterUser {

    @Headers({"Content-Type: application/json"})
    @POST("register")  // No leading slash!
    Call<APIResponse> registerUser(@Body RegisterModels visitors);
}