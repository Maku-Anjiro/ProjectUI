package com.example.uidesign.network.endpoints;

import com.example.uidesign.network.models.ApiSuccessfulResponse;
import com.example.uidesign.network.models.RegisterModels;
import com.example.uidesign.network.response.APIResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface UsersEndpoints {

    @Headers({"Content-Type: application/json"})
    @POST("register")
    Call<APIResponse> registerUser(@Body RegisterModels visitors);

    //Get the current user
    @Headers({"Content-Type: application/json"})
    @GET("personal/information")
    Call<ApiSuccessfulResponse> getCurrentUser(@Header("Authorization") String token);


    //Update user information

    @Multipart
    @PUT("information")
    Call<ApiSuccessfulResponse> updateInfo(@Part("full_name") RequestBody fullname,
                                           @Part MultipartBody.Part img_file,
                                           @Part("phone") RequestBody phoneNumber,
                                           @Header("Authorization") String token);

}