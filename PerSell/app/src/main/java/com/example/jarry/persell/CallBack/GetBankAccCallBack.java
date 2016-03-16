package com.example.jarry.persell.CallBack;

import com.example.jarry.persell.Entity.BankAcc;

import java.util.ArrayList;

/**
 * Created by Jarry on 23/2/2016.
 */
public interface GetBankAccCallBack {
    public abstract void done(ArrayList<BankAcc> bankAccsList);
}
