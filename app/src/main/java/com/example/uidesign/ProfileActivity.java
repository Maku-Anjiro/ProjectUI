package com.example.uidesign;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUserName, tvLocation, tvEmail, tvPhone;
    private Uri currentPhotoUri;
    private LinearLayout btnPrivacyPolicy, btnLogout;
    private Activity activity;
    private Context context;
    private MaterialButton btnEditProfile;
    private ImageView ivProfilePicture, ivProfilePictureEdit;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();
        // Logout

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        //show edit profile
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        //Toolbar
        setupToolbar();
    }
    //Setup toolbar
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void showEditProfileDialog() {
        Gson gson = new Gson();
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.drawable.screen_background_light);
        }
        // Get dialog views
        TextInputEditText etEditName = dialogView.findViewById(R.id.et_edit_name);
        TextInputEditText etEditEmail = dialogView.findViewById(R.id.et_edit_email);
        TextInputEditText etEditPhone = dialogView.findViewById(R.id.et_edit_phone);
//        TextInputEditText etEditAddress = dialogView.findViewById(R.id.et_edit_address);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.btn_save);
        TextView tvChangePhoto = dialogView.findViewById(R.id.tv_change_photo);
        ivProfilePictureEdit = dialogView.findViewById(R.id.iv_profile_picture_edit);

        // Change photo click
        tvChangePhoto.setOnClickListener(v -> {
            checkPermissionAndPickImage();
        });

        // Cancel button
        btnCancel.setOnClickListener(v -> {
            this.currentPhotoUri = null;
            dialog.dismiss();
        });
        // Save button
        btnSave.setOnClickListener(v -> {
            //TODO
        });
        // Fix 2: Safely show
        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }

    }


    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Clear user session
                    clearUserSession();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearUserSession() {
        // Clear SharedPreferences or any stored session data
        //TODO
    }



    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                showImagePickerDialog();
            }
        } else {
            // Android 6-12 requires READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                showImagePickerDialog();
            }
        }
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("Select Profile Picture")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Take Photo
                            openCamera();
                            break;
                        case 1: // Choose from Gallery
                            openGallery();
                            break;
                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void openCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        try {
            File photoFile = createImageFile();
            //convert File to Uri and set it to the global variable called currentPhotoUri
            currentPhotoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    photoFile);
            takePictureLauncher.launch(currentPhotoUri);
        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String imageFileName = "PROFILE_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    private void initViews() {
        activity = this;
        context = this;
        toolbar = findViewById(R.id.toolbar);
        tvUserName = findViewById(R.id.tv_user_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);

        btnEditProfile = findViewById(R.id.btn_edit_profile);

        btnLogout = findViewById(R.id.btn_logout);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);


    }

}
