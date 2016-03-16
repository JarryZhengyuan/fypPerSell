package com.example.jarry.persell.Enum;

/**
 * Created by Jarry on 1/2/2016.
 */
public enum ItemStatus {

    SOLD("Sold", 0),
    AVAILABLE("Available", 1),
    BOOKED("Booked", 2);

    private String stringValue;
    private int intValue;
    private ItemStatus(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    public int getIntValue() {
        return intValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
