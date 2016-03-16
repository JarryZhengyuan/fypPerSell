package com.example.jarry.persell.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.jarry.persell.Entity.RoomChat;
import com.example.jarry.persell.R;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

/**
 * Created by Jarry on 5/3/2016.
 */
public class RoomChatAdapter extends BaseAdapter implements Filterable {

    Context context;
    int layoutID;
    ArrayList<RoomChat> mStringFilterList =new ArrayList<>();
    ValueFilter valueFilter;
    ArrayList<RoomChat> rooms=new ArrayList<>();

    public RoomChatAdapter(Context context, int resource, ArrayList<RoomChat> objects) {
        this.context=context;
        this.layoutID=resource;
        this.rooms=objects;
        this.mStringFilterList=objects;
    }

    public void add(RoomChat room) {
        rooms.add(room);
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public RoomChat getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rooms.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutID, parent, false);

        RoomChat roomChat=rooms.get(position);
        String OwnerID=roomChat.getOwnerID();
        String SenderID=roomChat.getSenderID();
        int roomID=roomChat.getRoomID();

        TextView tvName = (TextView)rowView.findViewById(R.id.tvName);
        tvName.setText(SenderID);

        ProfilePictureView profilePictureView=(ProfilePictureView)rowView.findViewById(R.id.imageUser);
        profilePictureView.setProfileId(OwnerID);

        return rowView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<RoomChat> filterList = new ArrayList<RoomChat>();
                for (int i = 0; i < mStringFilterList.size(); i++) {

                    if ( (String.valueOf(mStringFilterList.get(i).getSenderID()).toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        RoomChat room = mStringFilterList.get(i);
                        filterList.add(room);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            rooms = (ArrayList<RoomChat>) results.values;
            notifyDataSetChanged();
        }

    }

}
