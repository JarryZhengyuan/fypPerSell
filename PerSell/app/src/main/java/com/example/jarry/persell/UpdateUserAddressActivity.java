package com.example.jarry.persell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class UpdateUserAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    String userid;
    private Button emailBtn,addressBtn,doneBtn,bankBtn,btnSelectState;
    private EditText etPoskod,etCity,etAddress;
    String[] values;
    List<State> type=new ArrayList<State>(EnumSet.allOf(State.class));
    private int stateID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_update_user_address);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Shipping Address");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(UpdateUserAddressActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        emailBtn=(Button)findViewById(R.id.emailBtn);
        emailBtn.setOnClickListener(this);
        addressBtn=(Button)findViewById(R.id.adressBtn);
        addressBtn.setOnClickListener(this);
        bankBtn=(Button)findViewById(R.id.bankBtn);
        bankBtn.setOnClickListener(this);
        btnSelectState=(Button)findViewById(R.id.btnSelectState);
        btnSelectState.setOnClickListener(this);
        doneBtn=(Button)findViewById(R.id.btnDone);
        doneBtn.setOnClickListener(this);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etCity=(EditText)findViewById(R.id.etCity);
        etPoskod=(EditText)findViewById(R.id.etPoskod);

        inputEditText();
    }

    private void inputEditText() {
        Address address=new Address();
        address.setUserID(userid);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchAddressDataInBackground(address, new GetAddressCallBack() {
            @Override
            public void done(Address address) {
                if(address.getStateID()!=-1){
                etAddress.setText(address.getAddress());
                etPoskod.setText(address.getPoskod());
                etCity.setText(address.getCity());
                btnSelectState.setText(type.get(address.getStateID()).toString());}
            }
        });
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.emailBtn:startActivity(new Intent(UpdateUserAddressActivity.this,UpdateUserProfileActivity.class));
                finish();
                break;
            case R.id.adressBtn:startActivity(new Intent(UpdateUserAddressActivity.this, UpdateUserAddressActivity.class));
                finish();
                break;
            case R.id.bankBtn:startActivity(new Intent(UpdateUserAddressActivity.this, UpdateUserBankActivity.class));
                finish();
                break;
            case R.id.btnDone:updateAddress();
                break;
            case R.id.btnSelectState:selectRegions();
                break;
        }
    }

    private void selectRegions() {
        values=new String[type.size()-1];
        for(int i=0;i<type.size()-1;i++){
            values[i]=type.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,values);


        new AlertDialog.Builder(this)
                .setTitle("Regions")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateID=which;
                        btnSelectState.setText(values[which]);
                    }
                })
                .create()
                .show();
    }

    private void updateAddress() {

        if(checkBlankEditText()==true){
        for(int i=0;i<values.length;i++){
            if(values[i].equals(btnSelectState.getText().toString())){
                stateID=i;
            }
        }}

        if(checkBlankEditText()!=false){
            Address address=new Address(etAddress.getText().toString(),etCity.getText().toString(),etPoskod.getText().toString(),stateID,userid);
            UserRequest userRequest=new UserRequest(this);
            userRequest.updateUserAddressDataInBackground(address, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                messsageToast("success");
                etCity.setText("");
                etPoskod.setText("");
                etAddress.setText("");
                btnSelectState.setText("Region");
            }
        });}
    }

    public Boolean checkBlankEditText(){
        if(etAddress.getText().toString().length()<10){
            etAddress.requestFocus();
            etAddress.setError("Please insert Address");
            return false;}
        if(etPoskod.getText().toString().length()<3){
            etPoskod.requestFocus();
            etPoskod.setError("Please insert Poskod");
            return false;}
        if(etCity.getText().toString().length()<3){
            etCity.requestFocus();
            etCity.setError("Please insert City");
            return false;}
        if(btnSelectState.getText().toString().equals("No Region Selected")){
            btnSelectState.requestFocus();
            btnSelectState.setError("Please select Region");
            return false;}

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(UpdateUserAddressActivity.this,ProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
