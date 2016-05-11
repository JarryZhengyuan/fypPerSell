package com.example.jarry.persell;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.SearchAdapter;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.ItemRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid,sql;
    private ListView list;
    Item item;
    Dialog dialog;
    private Button locBtn,categoryBtn;
    private SearchView searchView;
    SearchAdapter adapter;
    List<Category> category_type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    List<State> state_type=new ArrayList<State>(EnumSet.allOf(State.class));
    private int stateID=-1,categoryID=-1;
    private String sellerID="";
    String[] category_values;
    String[] state_values;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_search);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Search Items");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(SearchActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        categoryID=getIntent().getExtras().getInt("category_id");

        categoryBtn=(Button)findViewById(R.id.categoryBtn);
        locBtn=(Button)findViewById(R.id.locBtn);
        searchView=(SearchView)findViewById(R.id.searchView);
        list=(ListView)findViewById(R.id.listSearh);

        categoryBtn.setOnClickListener(this);
        locBtn.setOnClickListener(this);

        if(categoryID!=-1){
            categoryBtn.setText("Category     \n"+category_type.get(categoryID).toString()+"     ");
        }

        item=new Item();
        refreshList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.locBtn:selectState();
                break;
            case R.id.categoryBtn:selectCategory();
                break;
        }
    }

    private void selectCategory() {
        category_values=new String[category_type.size()];
        for(int i=0;i<category_type.size();i++){
           category_values[i]=category_type.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,category_values);

        new AlertDialog.Builder(this)
                .setTitle("Category")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryID=which;
                        categoryBtn.setText("Category     \n"+category_type.get(categoryID).toString()+"     ");
                        refreshList();
                    }
                })
                .create()
                .show();
    }

    private void selectState() {
        state_values=new String[state_type.size()];
        for(int i=0;i<state_type.size();i++){
            state_values[i]=state_type.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,state_values);

        new AlertDialog.Builder(this)
                .setTitle("Region")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateID=which;
                        locBtn.setText("Location     \n"+state_type.get(stateID).toString()+"     ");
                        refreshList();
                    }
                })
                .create()
                .show();
    }

    private void refreshList() {
        item.setItemTitle(getSQL());
        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchSearchedItemsDataInBackground(item, new GetAllItemCallBack() {
            @Override
            public void done(ArrayList<Item> items) {
                if(items.size()<0){
                    Toast.makeText(getApplicationContext(),"No found",Toast.LENGTH_LONG);
                }
                getItemData(items);
            }
        });
    }

    private void getItemData(ArrayList<Item> items) {
        adapter= new SearchAdapter(getApplicationContext(), R.layout.item_list,items);
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
                Intent intent = new Intent(SearchActivity.this, ItemOwnerActivity.class);
                intent.putExtra("item_id", item.getItemID());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(SearchActivity.this,HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public String getSQL(){
        if(stateID>=0 && stateID<state_type.size()-1){
            if(categoryID>=0 && categoryID<category_type.size()-1)
                sql="SELECT Item.*,User.UserName FROM Item JOIN User ON Item.UserID = User.UserID  WHERE Item.categoryID="+categoryID+" AND Item.stateID="+stateID+" AND Item.statusID=1 ORDER BY date DESC";
            else
                sql="SELECT Item.*,User.UserName FROM Item JOIN User ON Item.UserID = User.UserID  WHERE Item.stateID="+stateID+" AND Item.statusID=1 ORDER BY date DESC";
        }else{
            if(categoryID>=0 && categoryID<category_type.size()-1)
                sql="SELECT Item.*,User.UserName FROM Item JOIN User ON Item.UserID = User.UserID  WHERE Item.categoryID="+categoryID+" AND Item.statusID=1 ORDER BY date DESC";
            else
                sql="SELECT Item.*,User.UserName FROM Item JOIN User ON Item.UserID = User.UserID  WHERE Item.statusID=1 ORDER BY date DESC";
        }
        return sql;
    }
}
