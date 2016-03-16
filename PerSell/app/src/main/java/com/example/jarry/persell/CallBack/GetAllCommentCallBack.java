package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.Comment;

import java.util.ArrayList;

/**
 * Created by Jarry on 28/2/2016.
 */
public interface GetAllCommentCallBack {
    public abstract void done(ArrayList<Comment> comments);
}
