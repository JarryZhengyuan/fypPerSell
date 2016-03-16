package com.example.jarry.persell.Entity;

import android.graphics.Bitmap;

/**
 * Created by Jarry on 1/2/2016.
 */
public class User {

    String DB_UserID="UserID";
    String DB_name="name";
    String DB_phone="phone";
    String DB_email="email";
    String DB_facebook="facebook";
    String DB_picname="picname";

    String userID;
    String phone;
    String email;
    String userName;

    public User() {
    }

    public User(String userName,String email,String phone, String userID) {
        this.email = email;
        this.phone = phone;
        this.userID = userID;
        this.userName=userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserID() {
        return userID;
    }
}
