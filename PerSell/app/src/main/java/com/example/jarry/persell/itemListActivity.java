package com.example.jarry.persell;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jarry.persell.Adapter.BankAdapter;
import com.example.jarry.persell.Adapter.ItemAdapter;
import com.example.jarry.persell.Adapter.MyItemAdapter;
import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.CallBack.GetImageCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

public class itemListActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ListView list;
    MyItemAdapter adapter;
    String userid;
    int stateID=0;
    Item item;
    String ADD_ITEM="add_item";
    String EDIT_ITEM="edit_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_item_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Stocks");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(itemListActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        Address address=new Address();
        address.setUserID(userid);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchAddressDataInBackground(address, new GetAddressCallBack() {
            @Override
            public void done(Address address) {
                stateID = address.getStateID();
            }
        });

        refreshList();
    }

    private void refreshList() {
        item=new Item();
        item.setUserID(userid);

        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchMyItemsDataInBackground(item, new GetAllItemCallBack() {
            @Override
            public void done(ArrayList<Item> items) {
                if (items.size() < 1) {
                    messsageToast("null");
                } else {
                    getItemData(items);
                }
            }
        });
    }

    private void getItemData(ArrayList<Item> items) {
        list=(ListView)findViewById(R.id.itemList);
       adapter= new MyItemAdapter(getApplicationContext(), R.layout.item_list,items);
       list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = adapter.getItem(position);
                final CharSequence[] select = {"View", "Edit", "Delete", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(itemListActivity.this);
                builder.setTitle(item.getItemTitle());
                builder.setItems(select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (select[i].equals("View")) {
                            Intent intent = new Intent(itemListActivity.this, ItemActivity.class);
                            intent.putExtra("item_id", item.getItemID());
                            startActivity(intent);
                        } else if (select[i].equals("Edit")) {
                            if (checkItemStatus(item.getStatusID()) == false) {
                                messsageToast("You aren't permitted to change this Item");
                            } else {
                                Intent intent = new Intent(itemListActivity.this, itemInsertActivity.class);
                                intent.putExtra("item_process", EDIT_ITEM);
                                intent.putExtra("item_id", item.getItemID());
                                startActivity(intent);
                                finish();
                            }
                        } else if (select[i].equals("Delete")) {
                            if (checkItemStatus(item.getStatusID()) == false) {
                                messsageToast("You aren't permitted to detele this Item");
                            } else {
                                deleteItemSelected(item);
                            }
                        } else if (select[i].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void deleteItemSelected(final Item item) {
        new AlertDialog.Builder(itemListActivity.this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete :" + item.getItemTitle() + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ItemRequest itemRequest=new ItemRequest(itemListActivity.this);
                        itemRequest.deleteItemDataInBackground(item, new GetItemCallBack() {
                            @Override
                            public void done(Item item) {
                                messsageToast("Delete Success");
                                refreshList();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private Boolean checkItemStatus(int status){
        if(status==ItemStatus.BOOKED.getIntValue())
            return false;
        if(status==ItemStatus.SOLD.getIntValue())
            return false;
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(itemListActivity.this, HomeActivity.class));
            finish();
        }
        if (id == R.id.action_refresh) {
            refreshList();
        }
        if (id == R.id.action_add) {
            Intent intent=new Intent(itemListActivity.this, itemInsertActivity.class);
            intent.putExtra("item_process",ADD_ITEM);
            startActivity(intent);
            finish();
        }
        if (id == R.id.action_available) {
            adapter.getFilter().filter(String.valueOf(ItemStatus.AVAILABLE.getIntValue()));
        }
        if (id == R.id.action_booked) {
            adapter.getFilter().filter(String.valueOf(ItemStatus.BOOKED.getIntValue()));
        }
        if (id == R.id.action_sold) {
            adapter.getFilter().filter(String.valueOf(ItemStatus.SOLD.getIntValue()));
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
