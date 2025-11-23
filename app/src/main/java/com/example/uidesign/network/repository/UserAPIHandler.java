package com.example.uidesign.network.repository;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uidesign.network.APIBuilder;
import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.endpoints.GenerateQRCode;
import com.example.uidesign.network.endpoints.GetAllVisitors;
import com.example.uidesign.network.endpoints.RegisterUser;
import com.example.uidesign.network.models.AllVisitors;
import com.example.uidesign.network.models.RegisterModels;
import com.example.uidesign.network.response.APIResponse;
import com.example.uidesign.network.response.QrUrlResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAPIHandler {
    private Activity activity;
    private Context context;
    private APIBuilder api;

    public UserAPIHandler(Activity activity, Context context) {
        this.context = context;
        this.activity = activity;
        this.api = new APIBuilder(context);
    }

    public void registerUser(RegisterModels data, APICallbacks<APIResponse> callback) {
        RegisterUser apiService = api.getRetrofit().create(RegisterUser.class);

        Call<APIResponse> call = apiService.registerUser(data);

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                processResponse(response, callback);
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                Log.e("API_FAILURE", t.getMessage());
                callback.onError(t);
            }
        });
    }

    public void getQrImage(String qrCode, APICallbacks<byte[]> callback) {
        // handle qr code image
        String size = "200x200";
        GenerateQRCode generateQRCode = api.getRetrofitForQRCode().create(GenerateQRCode.class);
        generateQRCode.getQRCodeIMage(size, qrCode).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_SUCCESS", "Success: " + response.body().toString());
                    byte[] imageBytes = null;
                    try {
                        imageBytes = response.body().bytes();
                        callback.onSuccess(imageBytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String error = "Error";
                    try {
                        error = response.errorBody() != null ? response.errorBody().string() : "No body";
                    } catch (Exception e) {
                    }
                    Log.e("QR", "Code: " + response.code() + " | " + error);
                    callback.onError(new Exception("HTTP " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t);
            }
        });

    }

    public void getVisitors(APICallbacks<AllVisitors> callbacks){
        GetAllVisitors visitors = api.getRetrofit().create(GetAllVisitors.class);
        visitors.getAllVisitors().enqueue(new Callback<AllVisitors>() {
            @Override
            public void onResponse(Call<AllVisitors> call, Response<AllVisitors> response) {
                processResponse(response,callbacks);
            }

            @Override
            public void onFailure(Call<AllVisitors> call, Throwable t) {
            callbacks.onError(t);
            }
        });

    }

    private <T>  void processResponse(Response<T> response, APICallbacks<T> callback){
        if (response.isSuccessful() && response.body() != null) {
            Log.d("API_SUCCESS_FOR_VISITOR", "Success: " + response.body().toString());
            callback.onSuccess(response.body());
        } else {
            String error = "Error";
            try {
                error = response.errorBody() != null ? response.errorBody().string() : "No body";
            } catch (Exception e) {
            }
            Log.e("API_ERROR", "Code: " + response.code() + " | " + error);
            callback.onError(new Exception("HTTP " + response.code()));
        }
    }

}
