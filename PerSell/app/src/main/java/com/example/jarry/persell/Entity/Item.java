package com.example.jarry.persell.Entity;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Jarry on 1/2/2016.
 */
public class Item {

    String DB_itemID="itemID";
    String DB_categoryID="categoryID";
    String DB_itemPrice="itemPrice";
    String DB_pic1="pic1";
    String DB_pic2="pic2";
    String DB_pic3="pic3";
    String DB_itemDes="itemDes";
    String DB_statusID="statusID";
    String DB_UserID="UserID";
    String DB_itemTitle="itemTitle";

    int itemID;
    int categoryID;
    double itemPrice;
    String picname1,picname2,picname3;
    String date;
    String itemTitle,itemDes;
    int statusID;
    String UserID;
    int stateID;
    String username;

    public Item() {
    }

    public Item(String username,String itemTitle,String itemDes, int itemID, double itemPrice, String picname1, String picname2, String picname3, int stateID, int statusID, String userID, String date, int categoryID) {
        this.itemTitle=itemTitle;
        this.itemDes = itemDes;
        this.itemID = itemID;
        this.itemPrice = itemPrice;
        this.picname1 = picname1;
        this.picname2 = picname2;
        this.picname3 = picname3;
        this.stateID = stateID;
        this.statusID = statusID;
        UserID = userID;
        this.date = date;
        this.categoryID = categoryID;
        this.username=username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Item(String itemTitle,String itemDes, int itemID, double itemPrice, String picname1, String picname2, String picname3, int stateID, int statusID, String userID, String date, int categoryID) {
        this.itemTitle=itemTitle;
        this.itemDes = itemDes;
        this.itemID = itemID;
        this.itemPrice = itemPrice;
        this.picname1 = picname1;
        this.picname2 = picname2;
        this.picname3 = picname3;
        this.stateID = stateID;
        this.statusID = statusID;
        this.UserID = userID;
        this.date = date;
        this.categoryID = categoryID;
        this.username="";
    }

    public Item(Item item){
        this.itemTitle=item.getItemTitle();
        this.itemDes = item.getItemDes();
        this.itemID = item.getItemID();
        this.itemPrice = item.getItemPrice();
        this.picname1 = item.getPicname1();
        this.picname2 = item.getPicname2();
        this.picname3 = item.getPicname3();
        this.stateID = item.getStateID();
        this.statusID = item.getStatusID();
        this.UserID = item.getUserID();
        this.date = item.getDate();
        this.categoryID = item.getCategoryID();
        this.username=item.getUsername();
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemDes(String itemDes) {
        this.itemDes = itemDes;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setPicname1(String picname1) {
        this.picname1 = picname1;
    }

    public void setPicname2(String picname2) {
        this.picname2 = picname2;
    }

    public void setPicname3(String picname3) {
        this.picname3 = picname3;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getItemDes() {
        return itemDes;
    }

    public int getItemID() {
        return itemID;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String getPicname1() {
        return picname1;
    }

    public String getPicname2() {
        return picname2;
    }

    public String getPicname3() {
        return picname3;
    }

    public int getStateID() {
        return stateID;
    }

    public int getStatusID() {
        return statusID;
    }

    public String getUserID() {
        return UserID;
    }

    public String getDate() {
        return date;
    }

    public int getCategoryID() {
        return categoryID;
    }
}
