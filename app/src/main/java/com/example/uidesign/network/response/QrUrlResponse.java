package com.example.uidesign.network.response;

public class QrUrlResponse {
    private String qrUrl;

    public QrUrlResponse(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }
}