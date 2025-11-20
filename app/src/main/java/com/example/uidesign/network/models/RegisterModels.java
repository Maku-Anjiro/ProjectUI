package com.example.uidesign.network.models;

public class RegisterModels {
    String full_name, email, phone, purpose, host, notes;

    public RegisterModels(String full_name, String email, String phone, String purpose, String host, String notes) {
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.purpose = purpose;
        this.host = host;
        this.notes = notes;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
