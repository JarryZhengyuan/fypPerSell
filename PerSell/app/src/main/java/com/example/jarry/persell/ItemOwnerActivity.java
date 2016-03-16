package com.example.jarry.persell;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Comment;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
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
    private Button faceBtn,commentBtn,messageBtn,purchaseBtn;
    private ImageView pic1,pic2,pic3;
    private String userid,ownerid,ownerName;
    ActionBar actionBar;
    ProfilePictureView profilePictureView;
    private int item_id=-1;
    Item item;
    String actionTitle;
    List<Category> category_type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    List<State> state_type=new ArrayList<State>(EnumSet.allOf(State.class));

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

        faceBtn=(Button)findViewById(R.id.faceBtn);
        commentBtn=(Button)findViewById(R.id.commentBtn);
        messageBtn=(Button)findViewById(R.id.messageBtn);
        purchaseBtn=(Button)findViewById(R.id.purchaseBtn);

        faceBtn.setOnClickListener(this);
        commentBtn.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        purchaseBtn.setOnClickListener(this);
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
                if(i<len-1)
                    i++;
                imgView.setImageBitmap(bitmaps.get(i));
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i!=0)
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
    }

    private void getOwnerContact(final String ownerID) {
        User user=new User();
        user.setUserID(ownerID);

        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {

                ownerName=returnedUser.getUserName();
                profilePictureView.setProfileId(returnedUser.getUserID());
                tvOwner.setText(returnedUser.getUserName()+"\nEmail : " + returnedUser.getEmail() +
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

        faceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(String.valueOf(faceLink));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.faceBtn:
                try {
                    facebookLink(ownerid);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.img1:
            case R.id.img2:
            case R.id.img3:createDialog();
                break;
            case R.id.messageBtn:startMessage();
                break;
            case R.id.commentBtn:
                Intent intent=new Intent(ItemOwnerActivity.this, CommentActivity.class);
                intent.putExtra("item_id", item.getItemID());
                intent.putExtra("item_name", item.getItemTitle());
                startActivity(intent);
                break;
            case R.id.purchaseBtn:
                if(ownerid.equals(userid))
                    messsageToast("You are the Owner");
                else{
                Intent purintent=new Intent(ItemOwnerActivity.this, PurchaseActivity.class);
                purintent.putExtra("item_id", item.getItemID());
                    purintent.putExtra("owner_id", ownerid);
                startActivity(purintent);
                finish();}
                break;
        }
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
