package com.example.jarry.persell.Entity;

import java.util.Date;

/**
 * Created by Jarry on 1/2/2016.
 */
public class Comment {

    String DB_commentID="commentID";
    String DB_content="content";
    String DB_date="date";
    String DB_itemID="itemID";
    String DB_UserID="UserID";

    int commentID;
    String content;
    String date;
    int itemID;
    String userID;
    User user;

    public Comment() {
    }

    public Comment(int commentID, String content, String date, int itemID, String userID) {
        this.commentID = commentID;
        this.content = content;
        this.date = date;
        this.itemID = itemID;
        this.userID = userID;
    }

    public Comment(User user,int commentID, String content, String date, int itemID, String userID) {
        this.user=user;
        this.commentID = commentID;
        this.content = content;
        this.date = date;
        this.itemID = itemID;
        this.userID = userID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCommentID() {
        return commentID;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getItemID() {
        return itemID;
    }

    public String getUserID() {
        return userID;
    }
}
