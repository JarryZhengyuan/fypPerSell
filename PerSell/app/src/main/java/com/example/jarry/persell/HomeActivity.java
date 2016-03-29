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
import android.widget.ImageView;
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
import com.example.jarry.persell.Enum.Category;
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
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    User user;
    Address address;
    private String userid;
    ItemAdapter adapter;
    Item item;
    String userName;
    int i=0;
    ImageView sellBtn,findItem;
    ImageView electronicBtn,sportBtn;


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

        sellBtn=(ImageView)findViewById(R.id.sellBtn);
        findItem=(ImageView)findViewById(R.id.findItem);
        electronicBtn=(ImageView)findViewById(R.id.electronicBtn);
        sportBtn=(ImageView)findViewById(R.id.sportBtn);

        sellBtn.setOnClickListener(this);
        findItem.setOnClickListener(this);
        electronicBtn.setOnClickListener(this);
        sportBtn.setOnClickListener(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

        } else if (id == R.id.nav_search) {
            Intent intentSearch=new Intent(HomeActivity.this, SearchActivity.class);
            intentSearch.putExtra("category_id", -1);
            startActivity(intentSearch);
        } else if (id == R.id.nav_message) {
            startActivity(new Intent(HomeActivity.this, ConversationListActivity.class));
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            startActivity(new Intent(HomeActivity.this, FaceLoginActivity.class));
            finish();
        } else if (id == R.id.nav_item) {
            startActivity(new Intent(HomeActivity.this, itemListActivity.class));
        } else if (id == R.id.nav_bought) {
            startActivity(new Intent(HomeActivity.this, PurchaseListActivity.class));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sellBtn:
                Intent intent=new Intent(HomeActivity.this, itemInsertActivity.class);
                intent.putExtra("item_process", "add_item");
                startActivity(intent);
                break;
            case R.id.findItem:
                Intent intentSearch=new Intent(HomeActivity.this, SearchActivity.class);
                intentSearch.putExtra("category_id", -1);
                startActivity(intentSearch);
                break;
            case R.id.electronicBtn:
                Intent intentElec=new Intent(HomeActivity.this, SearchActivity.class);
                intentElec.putExtra("category_id", Category.ELECTRONIC.getIntValue());
                startActivity(intentElec);
                break;
            case R.id.sportBtn:
                Intent intentSport=new Intent(HomeActivity.this, SearchActivity.class);
                intentSport.putExtra("category_id", Category.SPORTs.getIntValue());
                startActivity(intentSport);
                break;
        }
    }
}
