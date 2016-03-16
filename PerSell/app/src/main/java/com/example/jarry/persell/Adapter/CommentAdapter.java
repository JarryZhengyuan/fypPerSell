package com.example.jarry.persell.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Comment;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.R;
import com.example.jarry.persell.Util.DateToString;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarry on 28/2/2016.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

    Context context;
    int layoutID;
    ArrayList<Comment> comments=new ArrayList<>();

    public CommentAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        this.context=context;
        this.layoutID=resource;
        this.comments=objects;
    }

    @Override
    public void add(Comment comment) {
        super.add(comment);
        comments.add(comment);
    }

    public Comment getComment(int position) {
        return comments.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutID, parent, false);

        Comment comment=getComment(position);
        final String userid=comment.getUserID();
        String message=comment.getContent();
        String date=comment.getDate();
        String name=comment.getUser().getUserName();

        DateToString dateToString=new DateToString();

        TextView tvName = (TextView)rowView.findViewById(R.id.tvName);
        TextView tvDate = (TextView)rowView.findViewById(R.id.tvDate);
        TextView tvContent = (TextView)rowView.findViewById(R.id.tvContent);

        ProfilePictureView profilePictureView=(ProfilePictureView)rowView.findViewById(R.id.imageUser);

        profilePictureView.setProfileId(userid);
        tvName.setText(name);
        tvDate.setText(dateToString.string2Time(date));
        tvContent.setText(message);

        return rowView;
    }
}
