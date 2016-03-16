package com.example.jarry.persell.Enum;

/**
 * Created by Jarry on 1/2/2016.
 */
public enum State {

    KUALA_LUMPUR("Kuala Lumpur", 0),
    SARAWAK("Sarawak", 1),
    PENANG("Penang", 2),
    MALACCA("Malacca", 3),
    NEGERI_SEMBILAN("Negeri Sembilan", 4),
    PAHANG("Pahang", 5),
    JOHOR("Johor", 6),
    TERENGGANU("Terengganu", 7),
    PERAK("Perak", 8),
    SABAH("Sabah", 9),
    PERLIS("Perlis", 10),
    KEDAH("Kedah", 11),
    KELANTAN("Kelantan", 12),
    SELANGOR("Selangor", 13),
    All("All", -1);

    private String stringValue;
    private int intValue;
    private State(String toString, int value) {
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
