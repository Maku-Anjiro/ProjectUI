package com.example.uidesign.network.endpoints;

import com.example.uidesign.network.models.ApiSuccessfulResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GoogleAuthenticateUser {
    @Headers({"Content-Type: application/json"})
    @POST("auth/google/callback")
    Call<ApiSuccessfulResponse> authenticateUser(@Body String token);
}
