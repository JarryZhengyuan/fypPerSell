package com.example.jarry.persell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.DateToString;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class itemInsertActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private EditText etAd,etItem,etItemTitle;
    private TextView text;
    private Button categorySelectedBtn,doneBtn,editBtn,regionBtn;
    private ImageView pic1,pic2,pic3;
    String[] values;
    List<Category> type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    private int categoryID=-1;
    private int SELECT_FILE=5;
    Item item;
    private String userid,pic1name="",pic2name="",pic3name="";
    Boolean pic1Status=false;
    Boolean pic2Status=false;
    Boolean pic3Status=false;
    byte img1[],img2[],img3[];
    private int item_id=-1;
    String item_process=null;
    String actionTitle,ADD_ITEM="add_item",EDIT_ITEM="edit_item";
    String[] statevalues;
    List<State> statetype=new ArrayList<State>(EnumSet.allOf(State.class));
    private int stateID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_item_insert);

        item_process=getIntent().getExtras().getString("item_process");
        doneBtn=(Button)findViewById(R.id.doneBtn);
        editBtn=(Button)findViewById(R.id.editBtn);
        regionBtn=(Button)findViewById(R.id.regionBtn);
        checkItemProcess(item_process);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(actionTitle);

        callbackManager=CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(itemInsertActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        text=(TextView)findViewById(R.id.text);
        etAd=(EditText)findViewById(R.id.etAd);
        etItem=(EditText)findViewById(R.id.etItem);
        etItemTitle=(EditText)findViewById(R.id.etItemTitle);
        categorySelectedBtn=(Button)findViewById(R.id.categorySelectedBtn);
        pic1=(ImageView)findViewById(R.id.pic1);
        pic2=(ImageView)findViewById(R.id.pic2);
        pic3=(ImageView)findViewById(R.id.pic3);

        categorySelectedBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        regionBtn.setOnClickListener(this);
        pic1.setOnClickListener(this);
        pic2.setOnClickListener(this);
        pic3.setOnClickListener(this);

        if(item_id!=-1)
        inputEditText(item_id);
    }

    private void inputEditText(int item_id) {
        Item item=new Item();
        item.setItemID(item_id);

        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchItemDataInBackground(item, new GetItemCallBack() {
            @Override
            public void done(Item item) {
                etItemTitle.setText(item.getItemTitle());
                etItem.setText(String.format("%.2f", item.getItemPrice()));
                etAd.setText(item.getItemDes());

                categorySelectedBtn.setText(type.get(item.getCategoryID()).toString());
                regionBtn.setText(statetype.get(item.getStateID()).toString());
                categoryID=item.getCategoryID();
                stateID=item.getStateID();

                text.setText("");

                Bitmap bitmap;
                if(item.getPicname1().length()>5){
                    bitmap=getBitmap(item.getPicname1());
                    img1=getByteArrayImg(bitmap);
                    pic1name=Base64.encodeToString(img1, Base64.DEFAULT);
                    pic1.setImageBitmap(bitmap);}
                if (item.getPicname2().length()>5){
                    bitmap=getBitmap(item.getPicname2());
                    img2=getByteArrayImg(bitmap);
                    pic2name=Base64.encodeToString(img2, Base64.DEFAULT);
                    pic2.setImageBitmap(bitmap);}
                if (item.getPicname3().length()>5){
                    bitmap=getBitmap(item.getPicname3());
                    img2=getByteArrayImg(bitmap);
                    pic2name=Base64.encodeToString(img2, Base64.DEFAULT);
                    pic2.setImageBitmap(bitmap);}
            }
        });
    }

    private Bitmap getBitmap(String picname) {
        Bitmap decodedByte = null;

            byte[] decodedString = Base64.decode(picname, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    private void checkItemProcess(String item_process) {
        if(item_process.equals(ADD_ITEM)){
           editBtn.setVisibility(Button.INVISIBLE);
            doneBtn.setVisibility(Button.VISIBLE);
            actionTitle="Sell Item";
        }else if(item_process.equals(EDIT_ITEM)){
           editBtn.setVisibility(Button.VISIBLE);
           doneBtn.setVisibility(Button.INVISIBLE);
            actionTitle="Edit Item";
            item_id=getIntent().getExtras().getInt("item_id");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.categorySelectedBtn: selectCategory();
                break;
            case R.id.doneBtn: insertItemData();
                break;
            case R.id.editBtn: updateItemData();
                break;
            case R.id.regionBtn: selectRegions();
                break;
            case R.id.pic1:updatePic(1);
                break;
            case R.id.pic2:updatePic(2);
                break;
            case R.id.pic3:updatePic(3);
                break;
        }
    }

    private void updateItemData() {
        if(checkBlank()!=false){
            item=new Item(etItemTitle.getText().toString(),etAd.getText().toString(),item_id,Double.parseDouble(etItem.getText().toString()),pic1name,pic2name,pic3name,stateID,1,userid,null,categoryID);
            ItemRequest itemRequest=new ItemRequest(this);
            itemRequest.editItemDataInBackground(item, new GetItemCallBack() {
                @Override
                public void done(Item item) {
                    messsageToast("Item is edited");
                    startActivity(new Intent(itemInsertActivity.this, itemListActivity.class));
                    finish();
                }
            });
        }
    }

    private void insertItemData() {

        DateToString dateToString=new DateToString();

        if(checkBlank()!=false){
        item=new Item(etItemTitle.getText().toString(),etAd.getText().toString(),-1,Double.parseDouble(etItem.getText().toString()),pic1name,pic2name,pic3name,stateID,1,userid,dateToString.date2String(),categoryID);
        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.storeItemDataInBackground(item, new GetItemCallBack() {
            @Override
            public void done(Item item) {
                messsageToast("Item is started to sell");
               startActivity(new Intent(itemInsertActivity.this, itemListActivity.class));
               finish();
            }
        });
        }
    }

    private void updatePic(final int picID) {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(itemInsertActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, picID);
                } else if (items[item].equals("Choose from Library")) {
                    Intent galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, SELECT_FILE+picID);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void selectCategory() {
        values=new String[type.size()-1];
        for(int i=0;i<type.size()-1;i++){
            values[i]=type.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,values);


        new AlertDialog.Builder(this)
                .setTitle("Category")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryID=which;
                        categorySelectedBtn.setText(values[which]);
                    }
                })
                .create()
                .show();
    }

    private void selectRegions() {
        statevalues=new String[statetype.size()-1];
        for(int i=0;i<statetype.size()-1;i++){
            statevalues[i]=statetype.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,statevalues);


        new AlertDialog.Builder(this)
                .setTitle("Regions")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stateID=which;
                        regionBtn.setText(statevalues[which]);
                    }
                })
                .create()
                .show();
    }

    public Boolean checkBlank(){
        if(etItemTitle.getText().toString().length()<3){
            etItemTitle.requestFocus();
            etItemTitle.setError("Please insert Ad title");
            return false;}
        if(etItem.getText().toString().length()<1){
            etItem.requestFocus();
            etItem.setError("Please insert Price");
            return false;}
        if(etAd.getText().toString().length()<3){
            etAd.requestFocus();
            etAd.setError("Please insert description");
            return false;}
        if(text.getText().toString().length()>3){
            text.requestFocus();
            text.setError("");
            messsageToast("Please choose photo");
            return false;}
        if(categorySelectedBtn.getText().toString().equals("No Category Selected")){
            categorySelectedBtn.requestFocus();
            categorySelectedBtn.setError("");
            messsageToast("Please select Category");
            return false;}
        if(regionBtn.getText().toString().equals("No Region Selected")){
            regionBtn.requestFocus();
            regionBtn.setError("");
            messsageToast("Please select Region");
            return false;}
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode==RESULT_OK){
            if(requestCode==1){
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                pic1.setImageBitmap(photo);
                text.setText("");
                pic1Status=true;
                img1=getByteArrayImg(photo);
                pic1name=Base64.encodeToString(img1, Base64.DEFAULT);

            }else if(requestCode==2) {
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                pic2.setImageBitmap(photo);
                text.setText("");
                pic2Status=true;
                img2=getByteArrayImg(photo);
                pic2name=Base64.encodeToString(img2, Base64.DEFAULT);

            }else if(requestCode==3){
                Bitmap photo=(Bitmap)data.getExtras().get("data");
                pic3.setImageBitmap(photo);
                text.setText("");
                pic3Status=true;
                img3=getByteArrayImg(photo);
                pic3name=Base64.encodeToString(img3, Base64.DEFAULT);

            }else if(requestCode==SELECT_FILE+1){
                Uri uri=data.getData();
                pic1.setImageURI(uri);
                text.setText("");
                pic1Status=true;
                Bitmap photo=((BitmapDrawable)pic1.getDrawable()).getBitmap();
                img1=getByteArrayImg(photo);
                pic1name=Base64.encodeToString(img1, Base64.DEFAULT);

            }else if(requestCode==SELECT_FILE+2){
                Uri uri=data.getData();
                pic2.setImageURI(uri);
                text.setText("");
                pic2Status=true;
                Bitmap photo=((BitmapDrawable)pic2.getDrawable()).getBitmap();
                img2=getByteArrayImg(photo);
                pic2name=Base64.encodeToString(img2, Base64.DEFAULT);

            }else if(requestCode==SELECT_FILE+3){
                Uri uri=data.getData();
                pic3.setImageURI(uri);
                text.setText("");
                pic3Status=true;
                Bitmap photo=((BitmapDrawable)pic3.getDrawable()).getBitmap();
                img3=getByteArrayImg(photo);
                pic3name=Base64.encodeToString(img3, Base64.DEFAULT);

            }
        }else if (resultCode == RESULT_CANCELED) {
            messsageToast("Picture was not taken");
        }
    }

    private byte[] getByteArrayImg(Bitmap photo) {
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
