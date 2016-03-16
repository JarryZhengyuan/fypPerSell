package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.MessageChat;

import java.util.ArrayList;

/**
 * Created by Jarry on 5/3/2016.
 */
public interface GetMessageCallBack {
    public abstract void done(ArrayList<MessageChat> messages);
}
