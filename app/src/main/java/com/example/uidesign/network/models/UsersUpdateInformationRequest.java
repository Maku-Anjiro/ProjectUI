package com.example.uidesign.network.models;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UsersUpdateInformationRequest {

    RequestBody requestFile;
    MultipartBody.Part imagePart;
    RequestBody fullnameBody;
    RequestBody emailBody;
    RequestBody contactNumberBody;


    public UsersUpdateInformationRequest(
            File imageFile,
            String fullname,
            String email,
            String phone
    ) {
        fullnameBody = RequestBody.create(MediaType.parse("text/plain"),fullname != null ? fullname : "");
        emailBody = RequestBody.create(MediaType.parse("text/plain"), email != null ? email : "");
        contactNumberBody = RequestBody.create(MediaType.parse("text/plain"),
                phone != null ? phone : "");

        // Handle nullable image safely
        if (imageFile != null) {
            requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("img_file", imageFile.getName(), requestFile);
        } else {
            requestFile = null;
            imagePart = null;
        }
    }

    public RequestBody getFullnameBody() {
        return fullnameBody;
    }

    public void setFullnameBody(RequestBody fullnameBody) {
        this.fullnameBody = fullnameBody;
    }

    public RequestBody getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(RequestBody emailBody) {
        this.emailBody = emailBody;
    }

    public RequestBody getContactNumberBody() {
        return contactNumberBody;
    }

    public void setContactNumberBody(RequestBody contactNumberBody) {
        this.contactNumberBody = contactNumberBody;
    }


    public RequestBody getRequestFile() {
        return requestFile;
    }

    public void setRequestFile(RequestBody requestFile) {
        this.requestFile = requestFile;
    }

    public MultipartBody.Part getImagePart() {
        return imagePart;
    }

    public void setImagePart(MultipartBody.Part imagePart) {
        this.imagePart = imagePart;
    }
}

