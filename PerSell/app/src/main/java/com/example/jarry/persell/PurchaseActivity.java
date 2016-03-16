package com.example.jarry.persell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.jarry.persell.Adapter.BankAdapter;
import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetBankAccCallBack;
import com.example.jarry.persell.CallBack.GetInvoiceCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Entity.Invoice;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.DateToString;
import com.example.jarry.persell.Util.PurchaseRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid;

    private Button doneBtn,btnSelectState,submitBtn,uploadRec;
    private EditText etPoskod,etCity,etAddress;
    String[] values;
    List<State> type=new ArrayList<State>(EnumSet.allOf(State.class));
    private int stateID=0;
    ImageView imageView;
    private ListView list;
    LinearLayout linearLayout;
    RelativeLayout linearAddres;
    int i=0;
    BankAcc bankAcc;
    ArrayList<BankAcc> bankAccs;
    String owner;
    BankAdapter adapter;
    PurchaseRequest purchaseRequest;
    private int item_id=-1;
    DateToString dateToString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_purchase);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Purchase");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivityForResult(new Intent(PurchaseActivity.this,FaceLoginActivity.class), 0);
        }
        userid=accessToken.getUserId();
        item_id=getIntent().getExtras().getInt("item_id");
        owner=getIntent().getExtras().getString("owner_id");

        btnSelectState=(Button)findViewById(R.id.btnSelectState);
        btnSelectState.setOnClickListener(this);
        doneBtn=(Button)findViewById(R.id.btnDone);
        doneBtn.setOnClickListener(this);
        uploadRec=(Button)findViewById(R.id.uploadRec);
        uploadRec.setOnClickListener(this);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etCity=(EditText)findViewById(R.id.etCity);
        etPoskod=(EditText)findViewById(R.id.etPoskod);
        imageView=(ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        list=(ListView)findViewById(R.id.listView);
        submitBtn=(Button)findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(this);
        linearLayout=(LinearLayout)findViewById(R.id.submitLayout);
        linearLayout.setVisibility(View.INVISIBLE);
        linearAddres=(RelativeLayout)findViewById(R.id.addressLayout);

        dateToString=new DateToString();
        purchaseRequest=new PurchaseRequest(this);
        inputEditText();
    }

    private void displayBank() {
        bankAcc=new BankAcc();
        bankAcc.setUserID(owner);
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
    }

    private void inputEditText() {
        Address address=new Address();
        address.setUserID(userid);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchAddressDataInBackground(address, new GetAddressCallBack() {
            @Override
            public void done(Address address) {
                if (address.getStateID() != -1) {
                    etAddress.setText(address.getAddress());
                    etPoskod.setText(address.getPoskod());
                    etCity.setText(address.getCity());
                    btnSelectState.setText(type.get(address.getStateID()).toString());
                    stateID=address.getStateID();}
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSelectState:
                selectRegions();
                break;
            case R.id.btnDone:
                if(checkBlank()==true)
                updateAddress();
                break;
            case R.id.imageView:
                updatePicture();
                break;
            case R.id.uploadRec:
                updatePicture();
                break;
            case R.id.submitBtn:
                if(checkImage()==true){
                    submitPayment();
                }
                break;
        }
    }

    private void submitPayment() {
        String picname=userid+item_id;
        Invoice invoice=new Invoice(userid,null,dateToString.date2String(),item_id,((BitmapDrawable)imageView.getDrawable()).getBitmap(),picname,1);
        purchaseRequest.updateInvoiceDataInBackground(invoice, new GetInvoiceCallBack() {
            @Override
            public void done(Invoice invoice) {
                messsageToast("Receipt is submitted");
                startActivity(new Intent(PurchaseActivity.this,PurchaseListActivity.class));
                finish();
            }
        });
    }

    public Boolean checkImage(){
        if(i>0)
            return true;
        else
        messsageToast("Please upload receipt");
        return false;
    }

    public Boolean checkBlank(){
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

    private void updatePicture() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (items[item].equals("Choose from Library")) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 2);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void updateAddress() {
       linearAddres.setVisibility(View.INVISIBLE);

        Address address=new Address(etAddress.getText().toString(),etCity.getText().toString(),etPoskod.getText().toString(),stateID,userid);
        Invoice invoice=new Invoice(userid,address,dateToString.date2String(),item_id,null,null,2);

        purchaseRequest.storeInvoiceDataInBackground(invoice, new GetInvoiceCallBack() {
            @Override
            public void done(Invoice invoice) {
                linearLayout.setVisibility(View.VISIBLE);
                displayBank();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==1){
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                i++;
            }else if(requestCode==2){
                Uri uri=data.getData();
                imageView.setImageURI(uri);
                i++;
            }
        }else if (resultCode == RESULT_CANCELED) {
            messsageToast("Picture was not taken");
        }
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
}
