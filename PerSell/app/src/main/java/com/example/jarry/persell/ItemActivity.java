package com.example.jarry.persell;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetInvoiceCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetPictureCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Invoice;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Enum.Category;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.DateToString;
import com.example.jarry.persell.Util.ItemRequest;
import com.example.jarry.persell.Util.PurchaseRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private TextView tvPrice,tvCategory,tvLocation,tvAd,tvOwner,tvShipping;
    private Button editBtn,receiptView;
    private ImageView pic1,pic2,pic3,faceBtn;
    private String userid,ownerid="";
    ActionBar actionBar;
    String EDIT_ITEM="edit_item";
    private int item_id=-1;
    Item item;
    String actionTitle;
    List<Category> category_type=new ArrayList<Category>(EnumSet.allOf(Category.class));
    List<State> state_type=new ArrayList<State>(EnumSet.allOf(State.class));
    LinearLayout linerBuyer;
    ProfilePictureView profilePictureView;
    DateToString dateToString;
    PurchaseRequest purchaseRequest;
    UserRequest userRequest;
    Bitmap bitReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_item);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(ItemActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        item_id=getIntent().getExtras().getInt("item_id");

        linerBuyer=(LinearLayout)findViewById(R.id.linerBuyer);
        tvPrice=(TextView)findViewById(R.id.tvPrice);
        tvCategory=(TextView)findViewById(R.id.tvCategory);
        tvLocation=(TextView)findViewById(R.id.tvLocation);
        tvAd=(TextView)findViewById(R.id.tvAd);
        pic1=(ImageView)findViewById(R.id.img1);
        pic2=(ImageView)findViewById(R.id.img2);
        pic3=(ImageView)findViewById(R.id.img3);
        tvShipping=(TextView)findViewById(R.id.tvShipping);
        tvOwner=(TextView)findViewById(R.id.tvOwner);
        receiptView=(Button)findViewById(R.id.receiptView);
        profilePictureView=(ProfilePictureView)findViewById(R.id.imageUser);

        faceBtn=(ImageView)findViewById(R.id.faceBtn);
        editBtn=(Button)findViewById(R.id.editBtn);
        editBtn.setOnClickListener(this);
        pic1.setOnClickListener(this);
        pic2.setOnClickListener(this);
        pic3.setOnClickListener(this);
        faceBtn.setOnClickListener(this);
        receiptView.setOnClickListener(this);

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
        dialog = new Dialog(ItemActivity.this);
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

        if(checkItemStatus(item.getStatusID())==false){
            linerBuyer.setVisibility(View.VISIBLE);
            editBtn.setVisibility(Button.GONE);
            getBuyerDetail(item_id);
        }
        else{
            linerBuyer.setVisibility(View.INVISIBLE);
            editBtn.setVisibility(Button.VISIBLE);}
    }

    private void getBuyerDetail(int item_id) {
        Invoice invoice=new Invoice();
        invoice.setItemID(item_id);
        dateToString=new DateToString();

        userRequest=new UserRequest(this);
        purchaseRequest=new PurchaseRequest(this);
        purchaseRequest.fetchInvoiceDataInBackground(invoice, new GetInvoiceCallBack() {
            @Override
            public void done(Invoice invoice) {
                if (invoice != null) {
                    String address = "\nPurchased on " + dateToString.string2Date(invoice.getDate()) + "\n\n" + "Shipping Address" +
                            invoice.getAdd().getAddress() + "\n" +
                            invoice.getAdd().getPoskod() + "   " +
                            invoice.getAdd().getCity() + "\n" +
                            state_type.get(invoice.getAdd().getStateID()).toString() + "\n";

                    ownerid = invoice.getUserID();
                    tvShipping.setText(address);
                    profilePictureView.setProfileId(ownerid);

                    User user = new User();
                    user.setUserID(ownerid);
                    userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
                        @Override
                        public void done(User returnedUser) {
                            tvOwner.setText(returnedUser.getUserName() + "\nEmail : " + returnedUser.getEmail() +
                                    "\n" + "(H/P) : " + returnedUser.getPhone());
                        }
                    });

                    Bitmap bitmap = null;
                    purchaseRequest.downloadPictureData(bitmap, invoice.getPicname(), new GetPictureCallBack() {
                        @Override
                        public void done(Bitmap bitmap) {
                            bitReceipt=bitmap;
                           // imgReceipt.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    messsageToast("null");
                }
            }
        });
    }

    private Boolean checkItemStatus(int status){
        if(status== ItemStatus.BOOKED.getIntValue())
            return false;
        if(status==ItemStatus.SOLD.getIntValue())
            return false;
        return true;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editBtn:
                Intent intent=new Intent(ItemActivity.this, itemInsertActivity.class);
                intent.putExtra("item_process", EDIT_ITEM);
                intent.putExtra("item_id", item_id);
                startActivity(intent);
                finish();
                break;
            case R.id.img1:
            case R.id.img2:
            case R.id.img3:createDialog();
                break;
            case R.id.faceBtn:
                try {
                    facebookLink(ownerid);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.receiptView:createReceiptView();
                break;
        }
    }

    private void createReceiptView() {
        Dialog dialog;
        dialog = new Dialog(ItemActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.receipt_view);
        dialog.setCancelable(true);

        ImageView imgViewReceipt=(ImageView)dialog.findViewById(R.id.receiptImage);
        imgViewReceipt.setImageBitmap(bitReceipt);

        dialog.show();
    }

    private void facebookLink(String userid) throws MalformedURLException {
        final URL faceLink = new URL("https://m.facebook.com/app_scoped_user_id/" + userid);

        faceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(String.valueOf(faceLink));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(ItemActivity.this, itemListActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
