package com.example.jarry.persell.Enum;

/**
 * Created by Jarry on 1/2/2016.
 */
public enum Bank {

    B("CIMB Bank", 0),
    PUBLIC_BANK("Public Bank", 1),
    MAYBANCIMK("Maybank", 2),
    HONGLEONG("Hong Leong Bank", 3);

    private String stringValue;
    private int intValue;
    private Bank(String toString, int value) {
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
