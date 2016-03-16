package com.example.jarry.persell.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Enum.Bank;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by Jarry on 23/2/2016.
 */
public class BankAdapter extends ArrayAdapter<BankAcc> {

    Context context;
    int layoutID;
    ArrayList<BankAcc> bankAccs=new ArrayList<>();
    List<Bank> type=new ArrayList<Bank>(EnumSet.allOf(Bank.class));

    public BankAdapter(Context context, int resource, ArrayList<BankAcc> objects) {
        super(context, resource, objects);
        this.context=context;
        this.layoutID=resource;
        this.bankAccs=objects;
    }

    @Override
    public void add(BankAcc bankAcc) {
        super.add(bankAcc);
        bankAccs.add(bankAcc);
    }

    @Override
    public BankAcc getItem(int position) {
        return bankAccs.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutID, parent, false);

        BankAcc bank = getItem(position);
        String acc=bank.getAccUser();
        String name=bank.getName();
        int bankID=bank.getBankID();
        String userid=bank.getUserID();
        String bankType=null;

        for(int i=0;i<type.size();i++){
            if(i==bankID){
                bankType=type.get(i).toString();
            }
        }

        TextView textViewItem = (TextView)rowView.findViewById(R.id.tvBankAcc);
        textViewItem.setText(bankType+"\n"+
                name+"\n"+
                acc+"\n");

        return rowView;
    }
}
