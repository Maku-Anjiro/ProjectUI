package com.example.uidesign.network.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllVisitors implements Serializable{
int  total_visitors,valid_qr_code,expired_qr_code,pending_qr_code;
    List<Visitor> data;

    public int getTotal_visitors() {
        return total_visitors;
    }

    public void setTotal_visitors(int total_visitors) {
        this.total_visitors = total_visitors;
    }

    public int getValid_qr_code() {
        return valid_qr_code;
    }

    public void setValid_qr_code(int valid_qr_code) {
        this.valid_qr_code = valid_qr_code;
    }

    public int getExpired_qr_code() {
        return expired_qr_code;
    }

    public void setExpired_qr_code(int expired_qr_code) {
        this.expired_qr_code = expired_qr_code;
    }

    public int getPending_qr_code() {
        return pending_qr_code;
    }

    public void setPending_qr_code(int pending_qr_code) {
        this.pending_qr_code = pending_qr_code;
    }

    public List<Visitor> getData() {
        return data;
    }

    public void setData(List<Visitor> data) {
        this.data = data;
    }

    public List<Visitor> getVisitors() {
        return data != null ? data : new ArrayList<Visitor>();
    }

    public void setVisitors(List<Visitor> visitors) {
        this.data = visitors;
    }

    public static class Visitor implements Serializable {
        private int visitor_id;
        private String full_name;
        private String email;
        private String purpose;
        private String host;
        private String qr_code;
        private String expiry_at;
        private String last_status;
        private String last_scan;
        private String created_at;
        private String phone;
        private String notes;

        // Empty constructor
        public Visitor() {}

        public Visitor(int visitor_id, String full_name, String email, String purpose, String host, String qr_code, String expiry_at, String last_status, String last_scan, String created_at, String phone, String notes) {
            this.visitor_id = visitor_id;
            this.full_name = full_name;
            this.email = email;
            this.purpose = purpose;
            this.host = host;
            this.qr_code = qr_code;
            this.expiry_at = expiry_at;
            this.last_status = last_status;
            this.last_scan = last_scan;
            this.created_at = created_at;
            this.phone = phone;
            this.notes = notes;
        }

        public int getVisitor_id() {
            return visitor_id;
        }

        public void setVisitor_id(int visitor_id) {
            this.visitor_id = visitor_id;
        }

        public String getFull_name() {
            return full_name != null ? full_name : "";
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getEmail() {
            return email != null ? email : "";
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPurpose() {
            return purpose != null ? purpose : "";
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose ;
        }

        public String getHost() {
            return host != null ? host : "";
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getQr_code() {
            return qr_code != null ? qr_code : "";
        }

        public void setQr_code(String qr_code) {
            this.qr_code = qr_code;
        }

        public String getExpiry_at() {
            return expiry_at != null ? expiry_at : "";
        }

        public void setExpiry_at(String expiry_at) {
            this.expiry_at = expiry_at;
        }

        public String getLast_status() {
            return last_status != null ? last_status : "";
        }

        public void setLast_status(String last_status) {
            this.last_status = last_status;
        }

        public String getLast_scan() {
            return last_scan != null ? last_scan : "";
        }

        public void setLast_scan(String last_scan) {
            this.last_scan = last_scan;
        }

        public String getCreated_at() {
            return created_at != null ? created_at : "";
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getPhone() {
            return phone != null ? phone : "";
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNotes() {
            return notes != null ? notes : "";
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }


}
