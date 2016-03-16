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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.BankAdapter;
import com.example.jarry.persell.CallBack.GetBankAccCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

public class UpdateUserBankActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ListView list;
    BankAdapter adapter;
    String userid;
    Dialog dialog;
    BankAcc bankAcc;
    RadioGroup radioGroup;
    EditText name;
    EditText text;
    ArrayList<BankAcc> bankAccs;
    private int bankID=-1;
    private Button emailBtn,addressBtn,bankBtn,btnBankType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_update_user_bank);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Personal Contact");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(UpdateUserBankActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        refreshList();

        emailBtn=(Button)findViewById(R.id.emailBtn);
        emailBtn.setOnClickListener(this);
        addressBtn=(Button)findViewById(R.id.adressBtn);
        addressBtn.setOnClickListener(this);
        bankBtn=(Button)findViewById(R.id.bankBtn);
        bankBtn.setOnClickListener(this);
        btnBankType=(Button)findViewById(R.id.btnBankType);
        btnBankType.setOnClickListener(this);
    }

    private void refreshList() {
        bankAcc=new BankAcc();
        bankAcc.setUserID(userid);
        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchAllBankDataInBackground(bankAcc, new GetBankAccCallBack() {
            @Override
            public void done(ArrayList<BankAcc> bankAccsList) {
                if (bankAccsList.size() < 1) {
                    messsageToast("null");
                } else {
                    getBankData(bankAccsList);
                }
            }
        });
    }

    private void getBankData(ArrayList<BankAcc> bankAccsList) {
        bankAccs=new ArrayList<>();
        bankAccs=bankAccsList;

       list=(ListView)findViewById(R.id.listView);
        adapter= new BankAdapter(getApplicationContext(), R.layout.list_bank,bankAccs);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bankAcc = adapter.getItem(position);
                new AlertDialog.Builder(UpdateUserBankActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete this Bank Account:" + bankAcc.getAccUser() + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                UserRequest userRequestDelete = new UserRequest(UpdateUserBankActivity.this);
                                userRequestDelete.deleteBankDataInBackground(bankAcc, new GetUserCallBack() {
                                    @Override
                                    public void done(User returnedUser) {
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
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.emailBtn:startActivity(new Intent(UpdateUserBankActivity.this, UpdateUserProfileActivity.class));
                finish();
                break;
            case R.id.adressBtn:startActivity(new Intent(UpdateUserBankActivity.this,UpdateUserAddressActivity.class));
                finish();
                break;
            case R.id.bankBtn:startActivity(new Intent(UpdateUserBankActivity.this,UpdateUserBankActivity.class));
                finish();
                break;
            case R.id.btnBankType:
                createDialog();
                break;
        }
    }

    private void createDialog() {

        dialog = new Dialog(UpdateUserBankActivity.this);
        dialog.setContentView(R.layout.maindialog);
        dialog.setTitle("Bank Account");
        dialog.setCancelable(true);

        text = (EditText) dialog.findViewById(R.id.etBankAcc);
        text.setHint("Insert Your Bank Acccount...");

        name = (EditText) dialog.findViewById(R.id.etName);
        name.setHint("Insert Your Name...");

       radioGroup=(RadioGroup)dialog.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCIMB:
                        bankID = 0;
                        break;
                    case R.id.rbPublic:
                        bankID = 1;
                        break;
                    case R.id.rbMay:
                        bankID = 2;
                        break;
                    case R.id.rbHL:
                        bankID = 3;
                        break;
                }
            }
        });

        Button btnDone=(Button)dialog.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBank(bankID, text.getText().toString(), name.getText().toString());
            }
        });
        dialog.show();
    }

    private void updateBank(int bankID,String acc,String name) {

        if(checkBlankEditText(bankID, acc, name)!=false){
        BankAcc bankAcc=new BankAcc(acc,bankID,name,userid);
        UserRequest userRequest=new UserRequest(this);
        userRequest.storeUserBankDataInBackground(bankAcc, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                messsageToast("success");
                dialog.dismiss();
                refreshList();
            }
        });
        }
    }

    public Boolean checkBlankEditText(int bankID,String acc,String accName){
        if(bankID<0){
            radioGroup.requestFocus();
            messsageToast("Please select Bank");
            return false;}
        if(acc.length()<7){
            text.requestFocus();
            text.setError("Please insert account number");
            return false;}
        if(accName.length()<7){
            name.requestFocus();
            name.setError("Please insert account number");
            return false;}
        return true;
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(UpdateUserBankActivity.this,ProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
