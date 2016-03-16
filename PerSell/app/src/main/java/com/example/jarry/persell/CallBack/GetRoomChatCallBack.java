package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.MessageChat;
import com.example.jarry.persell.Entity.RoomChat;

import java.util.ArrayList;

/**
 * Created by Jarry on 5/3/2016.
 */
public interface GetRoomChatCallBack {
    public abstract void done(ArrayList<RoomChat> rooms);
}
