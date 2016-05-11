package com.example.jarry.persell;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

public class UpdateUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    String userid;
    User user;
    private Button emailBtn,addressBtn,bankBtn;
    private EditText etEmail,etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_update_user_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Personal Contact");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(UpdateUserProfileActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        emailBtn=(Button)findViewById(R.id.emailBtn);
        emailBtn.setOnClickListener(this);
        addressBtn=(Button)findViewById(R.id.adressBtn);
        addressBtn.setOnClickListener(this);
        bankBtn=(Button)findViewById(R.id.bankBtn);
        bankBtn.setOnClickListener(this);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etPhone=(EditText)findViewById(R.id.etPhone);

        inputEditText();
    }

    private void inputEditText() {
        User user=new User();
        user.setUserID(userid);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                if(returnedUser.getEmail().length()>5){
                etEmail.setText(returnedUser.getEmail()); }
                if(returnedUser.getPhone().length()>5){
                    etPhone.setText(returnedUser.getPhone());}
            }
        });
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.emailBtn:
                startActivity(new Intent(UpdateUserProfileActivity.this, UpdateUserProfileActivity.class));
                finish();
                break;
            case R.id.adressBtn:
                startActivity(new Intent(UpdateUserProfileActivity.this, UpdateUserAddressActivity.class));
                finish();
                break;
            case R.id.bankBtn:
                startActivity(new Intent(UpdateUserProfileActivity.this, UpdateUserBankActivity.class));
                finish();
                break;
        }
    }

    private void updateContact() {
        user=new User(null,etEmail.getText().toString(),etPhone.getText().toString(),userid);

        if(checkBlankEditText()==false){
            messsageToast("Please don't leave blank data");
        }else{
        UserRequest userRequest=new UserRequest(this);
        userRequest.updateUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                messsageToast("updated success");
            }
        });}
    }

    public Boolean checkBlankEditText(){
        if(etEmail.getText().toString().length()<5){
            etEmail.requestFocus();
            etEmail.setError("Please insert Email correctly");
            return false;}
        if(etPhone.getText().toString().length()<5){
            etPhone.requestFocus();
            etPhone.setError("Minimum 10 digits");
            return false;}
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.updateprofile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(UpdateUserProfileActivity.this,ProfileActivity.class));
            finish();
        }

        if(id==R.id.action_save){
            updateContact();
        }
        return super.onOptionsItemSelected(item);
    }
}
