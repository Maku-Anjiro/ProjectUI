package com.example.uidesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.models.ApiSuccessfulResponse;
import com.example.uidesign.network.repository.UserAPIHandler;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private final String CLIENT_ID = "737144101830-rfdcp8nj2j1ae7b9qahabs0fca4v3a5m.apps.googleusercontent.com";
    private UserAPIHandler apiHandler;
    private Activity activity;
    private Context context;
    private Button googleSignInBtn;

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleSignInBtn = findViewById(R.id.google_sign_in_button);
        activity = this;
        context = this;

        apiHandler = new UserAPIHandler(activity, context);

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Log.i("ACCES_TOKEN", preferences.getString(Constants.ACCESS_TOKEN, ""));


        googleSignInBtn.setOnClickListener(v -> {
            apiHandler.googleLoginResponse(CLIENT_ID, new APICallbacks<ApiSuccessfulResponse>() {
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    String token = response.getAccess_token();
                    Log.i("ACCES_TOKEN", token);
                    SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constants.ACCESS_TOKEN, token);
                    editor.apply();
                    startActivity(new Intent(context, DashboardActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                @Override
                public void onError(Throwable t) {
                    Log.i("TEST_ERROR", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
}
