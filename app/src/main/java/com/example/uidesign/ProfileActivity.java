package com.example.uidesign;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.models.ApiSuccessfulResponse;
import com.example.uidesign.network.models.UsersUpdateInformationRequest;
import com.example.uidesign.network.repository.UserAPIHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUserName, tvLocation, tvEmail, tvPhone;
    private Uri currentPhotoUri;
    private LinearLayout btnPrivacyPolicy, btnLogout;
    private Activity activity;
    private Context context;
    private MaterialButton btnEditProfile;
    private AppUtility appUtility;
    private UserAPIHandler apiHandler;
    private ImageView ivProfilePicture, ivProfilePictureEdit;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initViews();

        setupActivityResultLaunchers();
        // Logout
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        //show edit profile
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        toolbar.setOnClickListener(v -> {
            onBackPressed();
        });


    }


    private void setupActivityResultLaunchers() {
        // Permission launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        showImagePickerDialog();
                    } else {
                        Toast.makeText(this, "Permission denied. Cannot access photos.", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        // Pick image from gallery
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //set the uri of select image to the global variable currentPhotoUri
                        currentPhotoUri = result.getData().getData();
                        if (currentPhotoUri != null) {
                            ivProfilePictureEdit.setImageURI(currentPhotoUri);

                        }
                    }
                }
        );

        // Take picture with camera
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && currentPhotoUri != null) {
                        ivProfilePictureEdit.setImageURI(currentPhotoUri);
                    }
                }
        );
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

        //Shared preference
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);

        String data = preferences.getString(Constants.USERS_DATA_KEY, "");

        String token = preferences.getString(Constants.ACCESS_TOKEN, "");

        if (!data.isEmpty()) {
            ApiSuccessfulResponse response = gson.fromJson(data, ApiSuccessfulResponse.class);
            etEditName.setText(response.getData().getUsers().getFull_name());
            etEditEmail.setText(response.getData().getUsers().getEmail());
            etEditPhone.setText(response.getData().getUsers().getPhone());
            Glide.with(context)
                    .load(response.getData().getUserProfile().getImg_url())
                    .circleCrop()
                    .into(ivProfilePictureEdit);
        }


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
            String fullname = Objects.requireNonNull(etEditName.getText()).toString();
            String phone = Objects.requireNonNull(etEditPhone.getText()).toString();
            String email = Objects.requireNonNull(etEditEmail.getText()).toString();

            //convert URI to File
            File imageFile = null;
            if (currentPhotoUri != null) {
                try {
                    imageFile = appUtility.uriToFile(currentPhotoUri, activity);
                } catch (IOException e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            UsersUpdateInformationRequest request = new UsersUpdateInformationRequest(imageFile, fullname, email, phone);

            apiHandler.updateUserInformation(request, token, new APICallbacks<ApiSuccessfulResponse>() {
                @Override
                public void onSuccess(ApiSuccessfulResponse response) {
                    SharedPreferences.Editor editor = preferences.edit();
                    String data = gson.toJson(response);
                    editor.putString(data, "");
                    editor.apply();
                    Toast.makeText(ProfileActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();

                    setCurrentUserData();

                    dialog.dismiss();
                }

                @Override
                public void onError(Throwable t) {

                }
            });
        });
        //
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
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

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


    //retrieved and set the current user in profile activity
    private void setCurrentUserData() {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_NAME, MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
        String token = preferences.getString(Constants.ACCESS_TOKEN, "");
        Log.i("ACCESS_TOKEN", token);
        apiHandler.getCurrentUser(token, new APICallbacks<ApiSuccessfulResponse>() {
            @Override
            public void onSuccess(ApiSuccessfulResponse response) {
                Gson gson = new Gson();
                String userData = gson.toJson(response, ApiSuccessfulResponse.class);

                editor.putString(Constants.USERS_DATA_KEY, userData);
                editor.apply();

                //set data frp, users
                tvUserName.setText(response.getData().getUsers().getFull_name());
                tvPhone.setText(response.getData().getUsers().getPhone());
                tvEmail.setText(response.getData().getUsers().getEmail());

                //Get profile picture
                Glide.with(context)
                        .load(response.getData().getUserProfile().getImg_url())
                        .circleCrop()
                        .into(ivProfilePicture);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        apiHandler = new UserAPIHandler(activity, context);
        appUtility = new AppUtility();

        btnLogout = findViewById(R.id.btn_logout);
        ivProfilePicture = findViewById(R.id.iv_profile_picture);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //get and set current data
        setCurrentUserData();
    }
}
