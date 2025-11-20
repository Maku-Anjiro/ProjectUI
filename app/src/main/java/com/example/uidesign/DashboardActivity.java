package com.example.uidesign;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



public class DashboardActivity extends AppCompatActivity {

    private TextView totalUsersCount, activeSessionsCount, logsCount;
    private ImageButton logOutB;

    // Firebase database reference


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board); // your XML

        // Initialize views
        totalUsersCount = findViewById(R.id.totalUsersCount);
        activeSessionsCount = findViewById(R.id.activeSessionsCount);
        logsCount = findViewById(R.id.logsCount);
        logOutB = findViewById(R.id.logOutB);

        // Logout button
        logOutB.setOnClickListener(v -> {
            // handle logout
            finish(); // just close activity for now
        });

    }

}
