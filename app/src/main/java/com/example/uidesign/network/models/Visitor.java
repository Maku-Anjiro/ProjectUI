package com.example.uidesign.network.models;

import java.io.Serializable;

public class Visitor implements Serializable {
    private String id;
    private String visitorName;
    private String email;
    private String purpose;
    private String host;
    private String qrCode;        // URL to QR image
    private String expiresAt;
    private String status;        // Valid, Expired, Pending
    private String lastScan;

    // Empty constructor
    public Visitor() {}

    // Full constructor
    public Visitor(String id, String visitorName, String email, String purpose,
                   String host, String qrCode, String expiresAt, String status, String lastScan) {
        this.id = id;
        this.visitorName = visitorName;
        this.email = email;
        this.purpose = purpose;
        this.host = host;
        this.qrCode = qrCode;
        this.expiresAt = expiresAt;
        this.status = status;
        this.lastScan = lastScan;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLastScan() { return lastScan != null && !lastScan.isEmpty() ? lastScan : "Never"; }
    public void setLastScan(String lastScan) { this.lastScan = lastScan; }
}