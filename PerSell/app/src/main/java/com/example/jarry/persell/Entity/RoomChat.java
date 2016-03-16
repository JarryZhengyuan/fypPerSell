package com.example.jarry.persell.Entity;

/**
 * Created by Jarry on 3/3/2016.
 */
public class RoomChat {

    int roomID;
    String OwnerID;
    String SenderID;
    String date;


    public RoomChat() {
    }

    public RoomChat(String date, String ownerID, int roomID, String senderID) {
        this.date = date;
        OwnerID = ownerID;
        this.roomID = roomID;
        SenderID = senderID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setOwnerID(String ownerID) {
        OwnerID = ownerID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getDate() {
        return date;
    }

    public String getOwnerID() {
        return OwnerID;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getSenderID() {
        return SenderID;
    }
}
