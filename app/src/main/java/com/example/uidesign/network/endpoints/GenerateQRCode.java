package com.example.uidesign.network.endpoints;

import com.example.uidesign.network.response.QrUrlResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface GenerateQRCode {

    @GET("create-qr-code/")
    Call<ResponseBody> getQRCodeIMage(@Query("size") String size, @Query("data") String data);

}
