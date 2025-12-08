package com.example.uidesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uidesign.adapter.VisitorAdapter;
import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.models.AllVisitors;
import com.example.uidesign.network.repository.UserAPIHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotal, tvValid, tvExpired, tvPending;
    private BottomNavigationView bottomNavigation;
    private EditText searchView; // Changed to EditText for Material design
    private Button btnExport, btnRefresh;
    private RecyclerView recyclerView;
    private VisitorAdapter adapter;
    private List<AllVisitors.Visitor> visitor = new ArrayList<>();
    private AllVisitors visitors;
    private UserAPIHandler apiHandler;
    private Context context;
    private Activity activity;
    private List<AllVisitors.Visitor> filteredVisitors = new ArrayList<>();

    // Firebase database reference


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board); // your XML

        context = this;
        activity = this;


        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        tvTotal = findViewById(R.id.tv_total);
        tvValid = findViewById(R.id.tv_valid);
        tvExpired = findViewById(R.id.tv_expired);
        tvPending = findViewById(R.id.tv_pending);
        searchView = findViewById(R.id.search_view);
        btnExport = findViewById(R.id.btn_export);
        btnRefresh = findViewById(R.id.btn_refresh);
        recyclerView = findViewById(R.id.recycler_visitors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiHandler = new UserAPIHandler(activity, context);
        adapter = new VisitorAdapter(this, filteredVisitors);
        recyclerView.setAdapter(adapter);
        visitors = new AllVisitors();



        applyFilter("All");

        // Setup bottom navigation
        setupBottomNavigation();

        // Search listener
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Filter buttons
        findViewById(R.id.btn_all).setOnClickListener(v -> applyFilter("All"));
        findViewById(R.id.btn_valid).setOnClickListener(v -> applyFilter("Valid"));
        findViewById(R.id.btn_expired).setOnClickListener(v -> applyFilter("Expired"));
        findViewById(R.id.btn_pending).setOnClickListener(v -> applyFilter("Pending"));

        btnRefresh.setOnClickListener(v -> {
            fetchData();
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        });


        //export data into csv file
        btnExport.setOnClickListener(v -> {
            exportCSV();

        });
    }

    private void fetchData() {
        visitor.clear();
        apiHandler.getVisitors(new APICallbacks<AllVisitors>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(AllVisitors response) {

                visitor.clear();
                visitor.addAll(response.getVisitors());

                filteredVisitors.clear();
                filteredVisitors.addAll(visitor);

                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    tvTotal.setText(String.valueOf(response.getTotal_visitors()));
                    tvValid.setText(String.valueOf(response.getValid_qr_code()));
                    tvExpired.setText(String.valueOf(response.getExpired_qr_code()));
                    tvPending.setText(String.valueOf(response.getPending_qr_code()));
                });

                Gson gson = new Gson();
                String data = gson.toJson(visitor);
            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }


    private void applyFilter(String status) {
        filteredVisitors.clear();
        if ("All".equals(status)) {
            filteredVisitors.addAll(visitor);
        } else {
            for (AllVisitors.Visitor v : visitor) {
                if (status.equals(v.getLast_status())) {
                    filteredVisitors.add(v);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterList(String query) {
        filteredVisitors.clear();
        for (AllVisitors.Visitor v : visitor) {
            if (v.getFull_name().toLowerCase().contains(query.toLowerCase()) ||
                    v.getLast_scan().toLowerCase().contains(query.toLowerCase())) {
                filteredVisitors.add(v);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void exportCSV() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, "visitors_export.csv");
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentResolver resolver = getContentResolver();

        Uri fileUri = resolver.insert(collection, values);

        if (fileUri == null) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OutputStream outputStream = resolver.openOutputStream(fileUri);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));

            // HEADER ROW
            writer.writeNext(new String[]{"ID", "Name", "Email", "Phone", "Purpose", "Notes",
                    "Host", "QR Code", "Last Scan", "Created At", "Expiry At",});


            for (AllVisitors.Visitor row : visitor) {
                writer.writeNext(new String[]{String.valueOf(row.getVisitor_id()), row.getFull_name(),
                        row.getEmail(), row.getPhone(), row.getPurpose(), row.getPurpose(), row.getNotes(),
                        row.getHost(), row.getQr_code(), row.getLast_scan(), row.getCreated_at(), row.getExpiry_at()});
            }

            writer.close();

            // Mark file as done writing
            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(fileUri, values, null, null);

            Toast.makeText(this, "CSV saved in Downloads!", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //to setup menu bottom
    private void setupBottomNavigation() {

        bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_profile) {
                    // Navigate to Profile Activity
                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }
}
