package com.example.jarry.persell.Enum;

/**
 * Created by Jarry on 1/2/2016.
 */
public enum Category {

    VEHICLE("Vehicle", 0),
    PROPERTIES("Properties", 1),
    ELECTRONIC("Electronic", 2),
    SPORTs("SPORTs", 3),
    HOME_OR_PERSONAL_ITEM("Home or Personal Item", 4),
    OTHERS("Others", 5),
    All("All", -1);

    private String stringValue;
    private int intValue;
    private Category(String toString, int value) {
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
