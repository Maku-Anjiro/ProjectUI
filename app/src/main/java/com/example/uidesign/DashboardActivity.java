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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uidesign.adapter.VisitorAdapter;
import com.example.uidesign.network.callbacks.APICallbacks;
import com.example.uidesign.network.models.AllVisitors;
import com.example.uidesign.network.repository.UserAPIHandler;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotal, tvValid, tvExpired, tvPending;
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

        // apiBuilder = new APIBuilder(this); // Comment out for mock data

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

        updateStats();
        applyFilter("All");

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
            // For mock: Just reload sample
            fetchData();
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        });


        //export data into csv file
        btnExport.setOnClickListener(v -> {
            exportCSV();

        });

        // Inside onCreate() after findViewById()
        findViewById(R.id.btnRegisterVisitor).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RegisterVisitor.class); // Change to your actual class name
            startActivity(intent);
            // Optional smooth transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void fetchData(){
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
                });

                Gson gson = new Gson();
                String data = gson.toJson(visitor);
                Log.i("DATA", data);
            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }

    private void updateStats() {
        int total = visitor.size();
        int valid = 0, expired = 0, pending = 0;
        for (AllVisitors.Visitor v : visitor) {
            switch (v.getLast_status()) {
                case "Valid":
                    valid++;
                    break;
                case "Expired":
                    expired++;
                    break;
                case "Pending":
                    pending++;
                    break;
            }
        }
        tvTotal.setText(String.valueOf(total));
        tvValid.setText(String.valueOf(valid));
        tvExpired.setText(String.valueOf(expired));
        tvPending.setText(String.valueOf(pending));
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


    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }
}
