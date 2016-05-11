package com.example.jarry.persell;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.SearchAdapter;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Util.ItemRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

public class SearchingActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid,sql;
    private ListView list;
    Item item;
    private SearchView searchView;
    SearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_searching);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search Items");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(SearchingActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        searchView=(SearchView)findViewById(R.id.searchView);
        list=(ListView)findViewById(R.id.listSearh);
        item=new Item();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    private void refreshList(String search) {
        item.setItemTitle(search);
        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchSearchingItemsDataInBackground(item, new GetAllItemCallBack() {
            @Override
            public void done(ArrayList<Item> items) {
                if (items.size() < 0) {
                    Toast.makeText(getApplicationContext(), "No found", Toast.LENGTH_LONG);
                }
                getItemData(items);
            }
        });
    }

    private void getItemData(ArrayList<Item> items) {
        adapter= new SearchAdapter(getApplicationContext(), R.layout.item_list,items);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item = adapter.getItem(position);
                Intent intent = new Intent(SearchingActivity.this, ItemOwnerActivity.class);
                intent.putExtra("item_id", item.getItemID());
                startActivity(intent);
                finish();
            }
        });
    }
}
