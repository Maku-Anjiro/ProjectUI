package com.example.uidesign;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.R;
import com.example.uidesign.adapter.VisitorAdapter;
import com.example.uidesign.network.models.Visitor;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotal, tvValid, tvExpired, tvPending;
    private EditText searchView; // Changed to EditText for Material design
    private Button btnExport, btnRefresh;
    private RecyclerView recyclerView;
    private VisitorAdapter adapter;
    private List<Visitor> visitors = new ArrayList<>();
    private List<Visitor> filteredVisitors = new ArrayList<>();

    // Firebase database reference


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board); // your XML



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

        adapter = new VisitorAdapter(this, filteredVisitors);
        recyclerView.setAdapter(adapter);

        loadSampleData(); // Use this instead of fetchVisitors()
        updateStats();
        applyFilter("All");

        // Search listener
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter buttons
        findViewById(R.id.btn_all).setOnClickListener(v -> applyFilter("All"));
        findViewById(R.id.btn_valid).setOnClickListener(v -> applyFilter("Valid"));
        findViewById(R.id.btn_expired).setOnClickListener(v -> applyFilter("Expired"));
        findViewById(R.id.btn_pending).setOnClickListener(v -> applyFilter("Pending"));

        btnRefresh.setOnClickListener(v -> {
            // For mock: Just reload sample
            loadSampleData();
            Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
        });

        // TODO: Implement Auto Refresh with Handler or Timer
        // TODO: Implement Export CSV (use CSVWriter library or manual)

        btnExport.setOnClickListener(v -> Toast.makeText(this, "Export CSV not implemented", Toast.LENGTH_SHORT).show());

        // Inside onCreate() after findViewById()
        findViewById(R.id.btnRegisterVisitor).setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, RegisterVisitor.class); // Change to your actual class name
            startActivity(intent);
            // Optional smooth transition
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void loadSampleData() {
        visitors.clear();
        // Sample data based on screenshot + current date Nov 22, 2025
        visitors.add(new Visitor("57", "Jenico Hasild Jidove", "", "Visit to QRgate system", "", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor57", "11/19/2025 11:16", "Expired", "11/19/2025 11:18"));
        visitors.add(new Visitor("56", "Jenico Hasild Jidove", "", "", "", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor56", "11/19/2025 11:15", "Expired", "11/19/2025 11:15"));
        visitors.add(new Visitor("54", "Manual Test", "manual@test.com", "", "", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor54", "11/19/2025 19:10", "Expired", "Never"));
        visitors.add(new Visitor("53", "Auto Test", "auto@test.com", "Meeting", "Host1", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor53", "11/22/2025 10:00", "Valid", "11/22/2025 09:45"));
        visitors.add(new Visitor("52", "Pending Visitor", "pending@example.com", "Interview", "HR", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor52", "11/23/2025 14:30", "Pending", "Never"));
        visitors.add(new Visitor("51", "Expired User", "expired@old.com", "Tour", "Guide", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor51", "11/21/2025 16:00", "Expired", "11/21/2025 15:55"));
        visitors.add(new Visitor("50", "Active Visitor", "active@now.com", "Conference", "Organizer", "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor50", "11/25/2025 18:00", "Valid", "11/22/2025 10:00"));
        // Add more as needed (total 41 for demo)
        for (int i = 40; i >= 1; i--) {
            String status = (i % 3 == 0) ? "Expired" : (i % 2 == 0) ? "Valid" : "Pending";
            visitors.add(new Visitor(String.valueOf(i), "Visitor " + i, "visitor" + i + "@example.com", "Purpose " + i, "Host " + i, "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=Visitor" + i, "11/" + (20 + i % 10) + "/2025 12:00", status, "11/22/2025 11:00"));
        }
        filteredVisitors.addAll(visitors);
        adapter.notifyDataSetChanged();
    }

    private void updateStats() {
        int total = visitors.size();
        int valid = 0, expired = 0, pending = 0;
        for (Visitor v : visitors) {
            switch (v.getStatus()) {
                case "Valid": valid++; break;
                case "Expired": expired++; break;
                case "Pending": pending++; break;
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
            filteredVisitors.addAll(visitors);
        } else {
            for (Visitor v : visitors) {
                if (status.equals(v.getStatus())) {
                    filteredVisitors.add(v);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterList(String query) {
        filteredVisitors.clear();
        for (Visitor v : visitors) {
            if (v.getVisitorName().toLowerCase().contains(query.toLowerCase()) ||
                    v.getId().contains(query) ||
                    v.getStatus().toLowerCase().contains(query.toLowerCase())) {
                filteredVisitors.add(v);
            }
        }
        adapter.notifyDataSetChanged();
    }

}
