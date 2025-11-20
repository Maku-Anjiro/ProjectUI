package com.example.uidesign.network.response;

public class APIResponse {
    String qr_code,email,phone,purpose,host,notes, expiry_at,visitor_name,current_time;
    int visitor_id;

    public String getHost() {
        return host != null ? host : "";
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPurpose() {
        return purpose != null ? purpose : "";
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getQr_Code() {
        return qr_code != null ? qr_code : "";
    }

    public void setQr_Code(String qr_Code) {
        this.qr_code = qr_Code;
    }

    public String getExpiry_at() {
        return expiry_at != null ? expiry_at : "";
    }

    public void setExpiry_at(String expiry_at) {
        this.expiry_at = expiry_at;
    }

    public int getVisitor_id() {
        return visitor_id;
    }

    public void setVisitor_id(int visitor_id) {
        this.visitor_id = visitor_id;
    }

    public String getVisitor_name() {
        return visitor_name != null ? visitor_name : "";
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getCurrent_time() {
        return current_time != null ? current_time : "";
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }
}
