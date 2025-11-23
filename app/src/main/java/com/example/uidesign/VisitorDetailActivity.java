package com.example.uidesign;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uidesign.network.models.AllVisitors;


public class VisitorDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_detail);

        AllVisitors.Visitor visitor = (AllVisitors.Visitor) getIntent().getSerializableExtra("visitor");

        if (visitor == null) {
            finish();
            return;
        }
        ((TextView) findViewById(R.id.tv_detail_id)).setText("ID: " + visitor.getVisitor_id());
        ((TextView) findViewById(R.id.tv_detail_name)).setText("Name: " + visitor.getFull_name());
        ((TextView) findViewById(R.id.tv_detail_email)).setText("Email: " + visitor.getEmail());
        ((TextView) findViewById(R.id.tv_detail_purpose)).setText("Purpose: " + visitor.getPurpose());
        ((TextView) findViewById(R.id.tv_detail_host)).setText("Host: " + visitor.getHost());
        ((TextView) findViewById(R.id.tv_detail_expires)).setText("Expires: " + visitor.getExpiry_at());
        ((TextView) findViewById(R.id.tv_detail_status)).setText("Status: " + visitor.getLast_status());
        ((TextView) findViewById(R.id.tv_detail_last_scan)).setText("Last Scan: " + visitor.getLast_scan());
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + visitor.getQr_code();

        ImageView ivQr = findViewById(R.id.iv_qr_detail);
        Glide.with(this).load(qrUrl).into(ivQr);
    }
}