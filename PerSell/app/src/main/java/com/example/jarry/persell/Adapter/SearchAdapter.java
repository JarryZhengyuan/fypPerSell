package com.example.jarry.persell.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.R;
import com.example.jarry.persell.Util.DateToString;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import android.widget.Filter;

/**
 * Created by Jarry on 2/3/2016.
 */
public class SearchAdapter extends BaseAdapter implements Filterable {

    Context context;
    int layoutID;
    ValueFilter valueFilter;
    ArrayList<Item> items=new ArrayList<>();
    ArrayList<Item> mStringFilterList =new ArrayList<>();
    List<Category> type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    List<State> statetype=new ArrayList<State>(EnumSet.allOf(State.class));
    List<ItemStatus> statusItem=new ArrayList<ItemStatus>(EnumSet.allOf(ItemStatus.class));

    public SearchAdapter(Context context, int resource, ArrayList<Item> objects) {
        this.context=context;
        this.layoutID=resource;
        this.items=objects;
        this.mStringFilterList=objects;
    }

    public void add(Item item) {
        items.add(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(layoutID, parent, false);

        Item item = getItem(position);
        double price=item.getItemPrice();
        int category=item.getCategoryID();
        String date=item.getDate();
        int status=item.getStatusID();
        int state=item.getStateID();
        String title=item.getItemTitle();

        DateToString dateToString=new DateToString();

        String stateT=null;
        String categoryType=null;
        String statusText=null;

        byte[] decodedString = Base64.decode(item.getPicname1(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        categoryType=type.get(category).toString();
        stateT=statetype.get(state).toString();
        statusText=statusItem.get(status).toString();
        String name=item.getUsername();

        TextView tvPrice = (TextView)rowView.findViewById(R.id.tvPrice);
        TextView tvCategory = (TextView)rowView.findViewById(R.id.tvCategory);
        TextView tvState = (TextView)rowView.findViewById(R.id.tvState);
        TextView tvDate = (TextView)rowView.findViewById(R.id.tvDate);
        ImageView img=(ImageView)rowView.findViewById(R.id.img);

        img.setImageBitmap(decodedByte);
        tvPrice.setText(title+"\n RM " + String.format("%.2f", price));
        tvCategory.setText(categoryType);
        tvDate.setText(dateToString.string2Date(date));
        tvState.setText(name+"\n"+stateT);

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
                ArrayList<Item> filterList = new ArrayList<Item>();
                for (int i = 0; i < mStringFilterList.size(); i++) {

                    if ( (mStringFilterList.get(i).getItemTitle().toUpperCase() )
                            .contains(constraint.toString().toUpperCase()) ||
                            (mStringFilterList.get(i).getUsername().toUpperCase() )
                                    .contains(constraint.toString().toUpperCase())) {
                       Item item = new Item(mStringFilterList.get(i));
                        filterList.add(item);
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
            items = (ArrayList<Item>) results.values;
            notifyDataSetChanged();
        }

    }
}
