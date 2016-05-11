package com.example.jarry.persell;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.CustomAdapter;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class CategoryActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    User user;
    private String userid;
    public static String [] prgmNameList={"Vehicle","Properties","Electronic","SPORTs","Home & Personal Item","Other"};
    public static int [] prgmImages={R.drawable.cat_vehicle,R.drawable.cat_properties,R.drawable.cat_electronic,
            R.drawable.cat_sport,R.drawable.cat_home,R.drawable.cat_other};
    GridView gv;
    CustomAdapter cusadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_category);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Categories");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(CategoryActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        gv=(GridView) findViewById(R.id.gridView);

        cusadapter=new CustomAdapter(this, prgmNameList, prgmImages);
        gv.setAdapter(cusadapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentSearch=new Intent(CategoryActivity.this, SearchActivity.class);
                intentSearch.putExtra("category_id", position);
                startActivity(intentSearch);
            }
        });

    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
