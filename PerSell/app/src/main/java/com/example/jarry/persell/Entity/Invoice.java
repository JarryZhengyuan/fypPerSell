package com.example.jarry.persell.Entity;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Jarry on 1/2/2016.
 */
public class Invoice {

    String date;
    Bitmap payPic;
    String picname;
    int itemID;
    int status;
    String UserID;
    Address add;

    public Invoice() {
        Address add=new Address();
    }

    public Invoice(String userid,Address add, String date, int itemID, Bitmap payPic, String picname, int status) {
        this.UserID=userid;
        this.add = add;
        this.date = date;
        this.itemID = itemID;
        this.payPic = payPic;
        this.picname = picname;
        this.status = status;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setAdd(Address add) {
        this.add = add;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setPayPic(Bitmap payPic) {
        this.payPic = payPic;
    }

    public void setPicname(String picname) {
        this.picname = picname;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Address getAdd() {
        return add;
    }

    public String getDate() {
        return date;
    }

    public int getItemID() {
        return itemID;
    }

    public Bitmap getPayPic() {
        return payPic;
    }

    public String getPicname() {
        return picname;
    }

    public int getStatus() {
        return status;
    }
}
