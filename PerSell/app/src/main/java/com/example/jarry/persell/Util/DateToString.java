package com.example.jarry.persell.Util;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jarry on 25/2/2016.
 */
public class DateToString {

    public DateToString() {
    }

    public String date2String(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    public String date2String2(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(date);
        return currentDateandTime;
    }

    public  CharSequence string2Date(String dd){
        Date temp = null;
        Date d = new Date();
        CharSequence s = null;
        try {
            temp = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(dd);
            s = DateFormat.format("MMMM d, yyyy ", temp.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    public  CharSequence string2Time(String dd){
        Date temp = null;
        Date d = new Date();
        CharSequence s = null;
        try {
            temp = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(dd);
            s = DateFormat.format("HH:mm", temp.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }
}
