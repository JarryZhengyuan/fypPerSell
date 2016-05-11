package com.example.jarry.persell;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.CustomAdapter;
import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Comment;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ItemOwnerActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private TextView tvPrice,tvCategory,tvLocation,tvAd,tvOwner;
    private ImageView pic1,pic2,pic3;
    private String userid,ownerid,ownerName;
    ActionBar actionBar;
    ProfilePictureView profilePictureView;
    private int item_id=-1;
    Item item;
    String actionTitle;
    List<Category> category_type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    List<State> state_type=new ArrayList<State>(EnumSet.allOf(State.class));
    public String [] prgmNameList={"Facebook","Message","Comment","Purchase"};
    public int [] prgmImages={R.drawable.item_facebook,R.drawable.item_message,R.drawable.item_comment,R.drawable.item_purchase};
    public String [] prgmNameListBook={"Facebook","Message","Comment","Purchase","Cancel"};
    public int [] prgmImagesBook={R.drawable.item_facebook,R.drawable.item_message,R.drawable.item_comment,R.drawable.item_purchase,R.drawable.item_cancel};
    GridView gv;
    CustomAdapter cusadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_item_owner);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(ItemOwnerActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        item_id=getIntent().getExtras().getInt("item_id");

        tvPrice=(TextView)findViewById(R.id.tvPrice);
        tvCategory=(TextView)findViewById(R.id.tvCategory);
        tvLocation=(TextView)findViewById(R.id.tvLocation);
        tvAd=(TextView)findViewById(R.id.tvAd);
        tvOwner=(TextView)findViewById(R.id.tvOwner);
        pic1=(ImageView)findViewById(R.id.img1);
        pic2=(ImageView)findViewById(R.id.img2);
        pic3=(ImageView)findViewById(R.id.img3);
        profilePictureView=(ProfilePictureView)findViewById(R.id.imageUser);
        gv=(GridView) findViewById(R.id.gridView);

        pic1.setOnClickListener(this);
        pic2.setOnClickListener(this);
        pic3.setOnClickListener(this);
        getItemData(item_id);
    }

    private void getItemData(int item_id) {
        item=new Item();
        item.setItemID(item_id);

        ItemRequest itemRequest=new ItemRequest(this);
        itemRequest.fetchItemDataInBackground(item, new GetItemCallBack() {
            @Override
            public void done(Item item) {
                displayView(item);
            }
        });
    }

    private static int i;
    ArrayList<Bitmap> bitmaps=new ArrayList<>();
    private void createDialog() {
        final int len=bitmaps.size();
        Dialog dialog;
        dialog = new Dialog(ItemOwnerActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picture_view);
        dialog.setCancelable(true);

        final ImageView imgView=(ImageView)dialog.findViewById(R.id.imageLarge);
        ImageView right=(ImageView)dialog.findViewById(R.id.next);
        ImageView left=(ImageView)dialog.findViewById(R.id.previous);

        imgView.setImageBitmap(bitmaps.get(i));

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < len - 1)
                    i++;
                imgView.setImageBitmap(bitmaps.get(i));
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i != 0)
                    i--;
                imgView.setImageBitmap(bitmaps.get(i));
            }
        });

        dialog.show();
    }

    private void displayView(Item item) {
        actionTitle=item.getItemTitle();
        actionBar.setTitle(actionTitle);
        tvPrice.setText("RM " + String.format("%.2f", item.getItemPrice()));
        tvAd.setText(item.getItemDes());
        tvCategory.setText(category_type.get(item.getCategoryID()).toString());
        tvLocation.setText(state_type.get(item.getStateID()).toString());

        pic1.setImageBitmap(getBitmap(item.getPicname1()));
        pic2.setImageBitmap(getBitmap(item.getPicname2()));
        pic3.setImageBitmap(getBitmap(item.getPicname3()));

        getOwnerContact(item.getUserID());

        if(item.getStatusID()== ItemStatus.BOOKED.getIntValue()){
            cusadapter=new CustomAdapter(this, prgmNameListBook, prgmImagesBook);
            gv.setNumColumns(5);
        }else{
            cusadapter=new CustomAdapter(this, prgmNameList, prgmImages);
        }

        gv.setAdapter(cusadapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                functionActivty(cusadapter.getItemName(position));
            }
        });
    }

    private void getOwnerContact(final String ownerID) {
        User user=new User();
        user.setUserID(ownerID);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {

                ownerName = returnedUser.getUserName();
                profilePictureView.setProfileId(returnedUser.getUserID());
                tvOwner.setText(returnedUser.getUserName() + "\nEmail : " + returnedUser.getEmail() +
                        "\n" + "(H/P) : " + returnedUser.getPhone());
                ownerid = returnedUser.getUserID();
            }
        });
    }

    private Bitmap getBitmap(String picname) {
        Bitmap decodedByte = null;
        if(picname.length()>5){
            byte[] decodedString = Base64.decode(picname, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            bitmaps.add(decodedByte);
        }
        return decodedByte;
    }

    private void facebookLink(String userid) throws MalformedURLException {
        final URL faceLink = new URL("https://m.facebook.com/app_scoped_user_id/" + userid);
                Uri uriUrl = Uri.parse(String.valueOf(faceLink));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img1:
            case R.id.img2:
            case R.id.img3:createDialog();
                break;
        }
    }

    private void functionActivty(String itemName) {
        if(itemName.equals(prgmNameList[0])){
            try {
                facebookLink(ownerid);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else if(itemName.equals(prgmNameList[1])){
            startMessage();
        }else if(itemName.equals(prgmNameList[2])){
            Intent intent=new Intent(ItemOwnerActivity.this, CommentActivity.class);
            intent.putExtra("item_id", item.getItemID());
            intent.putExtra("item_name", actionTitle);
            startActivity(intent);
        }else if(itemName.equals(prgmNameList[3])){
            if(ownerid.equals(userid))
                messsageToast("You are the Owner");
            else{
                Intent purintent=new Intent(ItemOwnerActivity.this, PurchaseActivity.class);
                purintent.putExtra("item_id", item.getItemID());
                purintent.putExtra("owner_id", ownerid);
                startActivity(purintent);
                finish();}
        }else if(itemName.equals(prgmNameListBook[4])){
            cancelOrder();
        }
    }

    private void cancelOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Order")
                .setMessage(item.getItemTitle())
                .setMessage("Are you sure want to cancel order of this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ItemRequest itemRequest=new ItemRequest(ItemOwnerActivity.this);
                        itemRequest.cancelBookedDataInBackground(item, new GetItemCallBack() {
                            @Override
                            public void done(Item item) {
                                messsageToast("Cancel Order Successful");
                                startActivity(new Intent(ItemOwnerActivity.this,PurchaseListActivity.class));
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.show();
    }

    private void startMessage() {
        if(ownerid.equals(userid))
            messsageToast("You are the Owner");
        else{
            Intent intent=new Intent(ItemOwnerActivity.this,MessagingActivity.class);
            intent.putExtra("ownerid",ownerid);
            intent.putExtra("ownerName",ownerName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(ItemOwnerActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
