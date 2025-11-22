package com.example.uidesign;



import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.uidesign.network.models.Visitor;


public class VisitorDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_detail);

        Visitor visitor = (Visitor) getIntent().getSerializableExtra("visitor");

        if (visitor == null) { finish(); return; }

        ((TextView)findViewById(R.id.tv_detail_id)).setText("ID: " + visitor.getId());
        ((TextView)findViewById(R.id.tv_detail_name)).setText("Name: " + visitor.getVisitorName());
        ((TextView)findViewById(R.id.tv_detail_email)).setText("Email: " + visitor.getEmail());
        ((TextView)findViewById(R.id.tv_detail_purpose)).setText("Purpose: " + visitor.getPurpose());
        ((TextView)findViewById(R.id.tv_detail_host)).setText("Host: " + visitor.getHost());
        ((TextView)findViewById(R.id.tv_detail_expires)).setText("Expires: " + visitor.getExpiresAt());
        ((TextView)findViewById(R.id.tv_detail_status)).setText("Status: " + visitor.getStatus());
        ((TextView)findViewById(R.id.tv_detail_last_scan)).setText("Last Scan: " + visitor.getLastScan());

        ImageView ivQr = findViewById(R.id.iv_qr_detail);
        Glide.with(this).load(visitor.getQrCode()).into(ivQr);
    }
}