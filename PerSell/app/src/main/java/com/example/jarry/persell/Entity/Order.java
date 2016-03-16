package com.example.jarry.persell.Entity;

/**
 * Created by Jarry on 1/2/2016.
 */
public class Order {

    String DB_orderID="orderID";
    String DB_itemID="itemID";
    String DB_statusO="statusO";

    int orderID;
    int itemID;
    int statusO;

    public Order() {
    }

    public Order(int itemID, int orderID, int statusO) {
        this.itemID = itemID;
        this.orderID = orderID;
        this.statusO = statusO;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setStatusO(int statusO) {
        this.statusO = statusO;
    }

    public int getItemID() {
        return itemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getStatusO() {
        return statusO;
    }
}
