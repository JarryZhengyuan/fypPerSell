package com.example.jarry.persell.Enum;

/**
 * Created by Jarry on 1/2/2016.
 */
public enum OrderStatus {

    CANCELLED("Cancelled", 0),
    PAID("Paid", 1),
    BOOKED("Booked", 2);

    private String stringValue;
    private int intValue;
    private OrderStatus(String toString, int value) {
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
