package com.example.jarry.persell;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.ItemAdapter;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Util.ItemRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

public class PurchaseListActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid;
    private ListView list;
    ItemAdapter adapter;
    Item item;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_purchase_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Your Purchases");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(PurchaseListActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        searchView=(SearchView)findViewById(R.id.sv);

        refreshList();
    }

    private void refreshList() {
        item=new Item();
        item.setUserID(userid);

        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchPurchaseItemsDataInBackground(item, new GetAllItemCallBack() {
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
        list=(ListView)findViewById(R.id.purchaseList);
        adapter= new ItemAdapter(getApplicationContext(), R.layout.item_list,items);
        list.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = adapter.getItem(position);

                if (item.getStatusID() == ItemStatus.BOOKED.getIntValue()) {
                    Intent intent = new Intent(PurchaseListActivity.this, ItemOwnerActivity.class);
                    intent.putExtra("item_id", item.getItemID());
                    startActivity(intent);
                } else if (item.getStatusID() == ItemStatus.SOLD.getIntValue()) {
                    Intent intent = new Intent(PurchaseListActivity.this,SoldItemActivity.class);
                    intent.putExtra("item_id", item.getItemID());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(PurchaseListActivity.this, HomeActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
