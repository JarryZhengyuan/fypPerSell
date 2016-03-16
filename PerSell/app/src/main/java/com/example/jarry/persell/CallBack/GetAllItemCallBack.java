package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.Item;

import java.util.ArrayList;

/**
 * Created by Jarry on 25/2/2016.
 */
public interface GetAllItemCallBack {
    public abstract void done(ArrayList<Item> items);
}
