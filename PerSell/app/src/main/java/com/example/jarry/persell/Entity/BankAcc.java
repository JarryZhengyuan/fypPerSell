package com.example.jarry.persell.Entity;

/**
 * Created by Jarry on 1/2/2016.
 */
public class BankAcc {

    String DB_accUser="accUser";
    String DB_bankID="bankID";
    String DB_UserID="UserID";
    String DB_name="name";

    String accUser;
    String userID;
    int bankID;
    String name;

    public BankAcc() {
    }

    public BankAcc(String accUser, int bankID, String name, String userID) {
        this.accUser = accUser;
        this.bankID = bankID;
        this.name = name;
        this.userID = userID;
    }

    public void setAccUser(String accUser) {
        this.accUser = accUser;
    }

    public void setBankID(int bankID) {
        this.bankID = bankID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAccUser() {
        return accUser;
    }

    public int getBankID() {
        return bankID;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }
}
