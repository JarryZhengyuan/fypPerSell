package com.example.jarry.persell.Entity;

/**
 * Created by Jarry on 4/3/2016.
 */
public class MessageChat {

    int messageID;
    int roomID;
    String date;
    String message;
    String userID;

    public MessageChat() {
    }

    public MessageChat(String date, String message, int messageID, int roomID,String userID) {
        this.date = date;
        this.message = message;
        this.messageID = messageID;
        this.roomID = roomID;
        this.userID = userID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public int getMessageID() {
        return messageID;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getUserID() {
        return userID;
    }
}
