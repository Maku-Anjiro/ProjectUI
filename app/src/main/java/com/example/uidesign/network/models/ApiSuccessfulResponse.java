package com.example.uidesign.network.models;

import java.io.Serializable;

public class ApiSuccessfulResponse implements Serializable {
    String message, status_message, access_token, action;
    int status_code;
    Data data;

    public Data getData() {
        return data != null ? data : new Data();
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }


    public static class Data{
        UserData Users;
        UserProfilePicture UserProfile;

        public UserData getUsers() {
            return Users;
        }

        public void setUsers(UserData users) {
            Users = users;
        }

        public UserProfilePicture getUserProfile() {
            return UserProfile;
        }

        public void setUserProfile(UserProfilePicture userProfile) {
            UserProfile = userProfile;
        }
    }

    public static class UserData{

        public UserData(String phone, String full_name, String status, String email, int user_id) {
            this.phone = phone;
            this.full_name = full_name;
            this.status = status;
            this.email = email;
            this.user_id = user_id;
        }
        public  UserData(){
        }

        String phone, full_name,status,email;
        int user_id;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getFull_name() {
            return full_name;
        }

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
    public static class UserProfilePicture{
        int id;
        String public_key, img_url;

        public UserProfilePicture(int id, String public_key, String img_url) {
            this.id = id;
            this.public_key = public_key;
            this.img_url = img_url;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPublic_key() {
            return public_key;
        }

        public void setPublic_key(String public_key) {
            this.public_key = public_key;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }
}
