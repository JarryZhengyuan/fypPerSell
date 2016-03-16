package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Entity.User;

import java.util.ArrayList;

/**
 * Created by Jarry on 22/2/2016.
 */
public interface GetUserCallBack {
    public abstract void done(User returnedUser);
}
