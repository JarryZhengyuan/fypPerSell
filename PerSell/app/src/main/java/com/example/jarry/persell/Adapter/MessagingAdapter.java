package com.example.jarry.persell.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jarry.persell.Entity.MessageChat;
import com.example.jarry.persell.R;
import com.example.jarry.persell.Util.DateToString;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

/**
 * Created by Jarry on 4/3/2016.
 */
public class MessagingAdapter extends BaseAdapter {

    private ArrayList<MessageChat> messageChats;
    private LayoutInflater mInflater;
    private String userid,ownerName,username;

    public MessagingAdapter(Activity activity,String userid,ArrayList<MessageChat> messageChats,String username,String ownerName) {
        mInflater = activity.getLayoutInflater();
        this.messageChats = messageChats;
        this.userid=userid;
        this.username=username;
        this.ownerName=ownerName;
    }

    public void addMessage(MessageChat message,int direction) {

        if(direction==1){
        if(messageChats.size()!=0){
            if(checkSimilar(message)){
                messageChats.add(message);
                notifyDataSetChanged();
            }
        }else{
            messageChats.add(message);
            notifyDataSetChanged();
        }}
        else if(direction==0){
            messageChats.add(message);
            notifyDataSetChanged();
        }
    }

    public Boolean checkSimilar(MessageChat message){
        for(int i=0;i<messageChats.size();i++){
            if(messageChats.get(i).getUserID().equals(message.getUserID()) && messageChats.get(i).getMessage().equals(message.getMessage())
                    && messageChats.get(i).getDate().equals(message.getDate())){
                return false;
            }
        }
        return true;
    }

    public void addMessages(ArrayList<MessageChat> messages) {
        messageChats=messages;
    }

    @Override
    public int getCount() {
        return messageChats.size();
    }

    @Override
    public Object getItem(int i) {
        return messageChats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageChat msg = messageChats.get(i);
        String name="";
            int res = 0;
            if (msg.getUserID().equals(userid)){
                res = R.layout.message_right;
                name=username;
            }
            else{
                res = R.layout.message_left;
                name=ownerName;
            }

            convertView = mInflater.inflate(res, viewGroup, false);


        DateToString date=new DateToString();

        ProfilePictureView profilePictureView=(ProfilePictureView)convertView.findViewById(R.id.imageUser);

        profilePictureView.setProfileId(userid);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);
        TextView tvName=(TextView)convertView.findViewById(R.id.tvName);

        profilePictureView.setProfileId(msg.getUserID());
        tvName.setText(name);
        txtMessage.setText(msg.getMessage());
        txtDate.setText("     "+date.string2Time(msg.getDate())+"     ");

        return convertView;
    }
}
