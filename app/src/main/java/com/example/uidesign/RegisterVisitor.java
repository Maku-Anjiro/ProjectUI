package com.example.uidesign;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.uidesign.network.APIBuilder;
import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.models.RegisterModels;
import com.example.uidesign.network.repository.UserAPIHandler;
import com.example.uidesign.network.response.APIResponse;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RegisterVisitor extends AppCompatActivity {


    // Fields
    private EditText inputFullName, inputEmail, inputPhone, inputHost, inputNotes;
    private ImageView qrCodeImage;
    private LinearLayout qrSection;

    private TextView tvQRCodeID, tvName, tvExpiresAt, tvEmail, tvPhone, tvPurpose, tvHost, tvNotes;
    private Spinner spinnerPurpose;

    private Button btnGenerateQR, btnClear,btnLogin;
    private Context context;
    private Activity activity;
    private UserAPIHandler apiHandler;
    private APIBuilder apiBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        activity = this;
        context = this;
        // ==== FIND UI COMPONENTS ====
        inputFullName = findViewById(R.id.inputFullName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputHost = findViewById(R.id.inputHost);
        inputNotes = findViewById(R.id.inputNotes);
        spinnerPurpose = findViewById(R.id.spinnerPurpose);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        apiHandler = new UserAPIHandler(activity, context);

        qrSection = findViewById(R.id.qrSection);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        tvQRCodeID = findViewById(R.id.tvQRCodeID);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvPurpose = findViewById(R.id.tvPurpose);
        tvHost = findViewById(R.id.tvHost);
        tvNotes = findViewById(R.id.tvNotes);
        btnClear = findViewById(R.id.btnClear);
        tvExpiresAt = findViewById(R.id.tvExpiresAt);
        btnLogin = findViewById(R.id.btnLogin);

        // ==== SPINNER LIST ====
        List<String> purposeList = new ArrayList<>();
        purposeList.add("Select Purpose of Visit");
        purposeList.add("Meeting");
        purposeList.add("Delivery");
        purposeList.add("Appointment");
        purposeList.add("Walk-in");
        purposeList.add("Others");
        apiBuilder = new APIBuilder(context);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                purposeList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPurpose.setAdapter(adapter);
        spinnerPurpose.setSelection(0);


        btnGenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fullName = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();
                String purpose = spinnerPurpose.getSelectedItem().toString();
                String host = inputHost.getText().toString().trim();
                String notes = inputHost.getText().toString().trim();

                // ==== VALIDATION ====
                if (fullName.isEmpty()) {
                    inputFullName.setError("Full name required");
                    return;
                }

                if (email.isEmpty()) {
                    inputEmail.setError("Email required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.setError("Invalid email");
                    return;
                }

                if (phone.isEmpty()) {
                    inputPhone.setError("Phone number required");
                    return;
                }

                if (phone.length() < 10) {
                    inputPhone.setError("Invalid phone number");
                    return;
                }

                if (purpose.equals("Select Purpose of Visit")) {
                    Toast.makeText(RegisterVisitor.this, "Please select purpose", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (host.isEmpty()) {
                    inputHost.setError("Host person required");
                    return;
                }
                RegisterModels userModel = new RegisterModels(fullName,
                        email,
                        phone,
                        purpose,
                        host,
                        notes);
                apiHandler.registerUser(userModel, new APICallbacks<APIResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(APIResponse response) {

                                String utcString = "2025-11-21T07:03:00.860336+00:00";

                                // Parse UTC string
                                OffsetDateTime utcDateTime = OffsetDateTime.parse(utcString);

                                // Convert to Philippine Time (UTC+8)
                                ZoneId philippineZone = ZoneId.of("Asia/Manila");
                                var phTime = utcDateTime.atZoneSameInstant(philippineZone);

                                // Format output
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                                String formatted = phTime.format(formatter);

                                btnClear.setVisibility(View.VISIBLE);
                                //set text for expiration with formatted date and time
                                tvExpiresAt.setText(formatted);
                                tvQRCodeID.setText(response.getQr_Code());
                                tvName.setText(response.getVisitor_name());
                                tvEmail.setText(response.getEmail());
                                tvPhone.setText(response.getPhone());
                                tvPurpose.setText(response.getPurpose());
                                tvHost.setText(response.getHost());
                                tvNotes.setText(response.getNotes());
                                String qrUrl =  "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data="+response.getQr_Code();
                        Glide.with(context).load(qrUrl).into(qrCodeImage);

                                //then set visibility for qr section
                                qrSection.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
