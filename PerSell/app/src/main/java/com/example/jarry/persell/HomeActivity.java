package com.example.jarry.persell;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jarry.persell.Adapter.ItemAdapter;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.SinchService;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.daimajia.androidanimations.library.Techniques.*;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    User user;
    Address address;
    private String userid;
    private ListView list;
    ItemAdapter adapter;
    Item item;
    String userName;
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PerSell");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivityForResult(new Intent(HomeActivity.this,FaceLoginActivity.class), 0);
        }
        userid=accessToken.getUserId();

        user=new User();
        user.setUserID(userid);
        address=new Address();
        address.setUserID(userid);
        address.setStateID(-1);

        loginUser();
        refreshList();

    }

    private void refreshList() {
        item=new Item();
        item.setStatusID(1);

        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchAvailableItemsDataInBackground(item, new GetAllItemCallBack() {
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
        list=(ListView)findViewById(R.id.itemHomeList);
        adapter= new ItemAdapter(getApplicationContext(), R.layout.item_list,items);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                item=adapter.getItem(position);
                Intent intent = new Intent(HomeActivity.this, ItemOwnerActivity.class);
                intent.putExtra("item_id", item.getItemID());
                startActivity(intent);
            }
        });
    }

    private void loginUser() {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            userName=object.getString("name");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        user.setUserName(userName);
                        UserRequest userRequest=new UserRequest(HomeActivity.this);
                        userRequest.storeUserDataInBackground(user, new GetUserCallBack() {
                            @Override
                            public void done(User returnedUser) {
                                messsageToast("Login Success");
                            }
                        });
                        userRequest.storeUserAddressDataInBackground(address, new GetUserCallBack() {
                            @Override
                            public void done(User returnedUser) {
                                //messsageToast("Address Success");
                            }
                        });

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,link,location");
        request.setParameters(parameters);
        request.executeAsync();


    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        i = 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            i++;
            if (i == 1) {
                Toast.makeText(HomeActivity.this, "Press back once more to exit.",
                        Toast.LENGTH_SHORT).show();
            } else if(i>1) {
                finish();
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshList();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_search) {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            finish();
        } else if (id == R.id.nav_message) {
            startActivity(new Intent(HomeActivity.this, ConversationListActivity.class));
            finish();
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            startActivity(new Intent(HomeActivity.this, FaceLoginActivity.class));
            finish();
        } else if (id == R.id.nav_item) {
            startActivity(new Intent(HomeActivity.this, itemListActivity.class));
            finish();
        } else if (id == R.id.nav_bought) {
            startActivity(new Intent(HomeActivity.this, PurchaseListActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            this.finish();
        }
    }
}
