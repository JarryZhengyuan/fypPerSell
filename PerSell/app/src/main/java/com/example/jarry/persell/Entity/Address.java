package com.example.jarry.persell.Entity;

/**
 * Created by Jarry on 1/2/2016.
 */
public class Address {

    String DB_poskod="poskod";
    String DB_city="city";
    String DB_stateID="stateID";
    String DB_address="address";
    String DB_UserID="UserID";

    String poskod;
    String city;
    int stateID;
    String address;
    String userID;

    public Address() {
    }

    public Address(String address, String city, String poskod, int stateID, String userID) {
        this.address = address;
        this.city = city;
        this.poskod = poskod;
        this.stateID = stateID;
        this.userID = userID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPoskod(String poskod) {
        this.poskod = poskod;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPoskod() {
        return poskod;
    }

    public int getStateID() {
        return stateID;
    }

    public String getUserID() {
        return userID;
    }
}
