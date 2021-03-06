package com.example.jarry.persell;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetBankAccCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Enum.Bank;
import com.example.jarry.persell.Enum.ItemStatus;
import com.example.jarry.persell.Enum.State;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    List<State> type=new ArrayList<State>(EnumSet.allOf(State.class));
    List<Bank> banktype=new ArrayList<Bank>(EnumSet.allOf(Bank.class));
    private Button editBtn;
    private TextView tvEmail,tvName,tvAddress,tvBank,tvAgeGender;
    ProfilePictureView profilePictureView;
    User user;
    Address address;
    BankAcc bankAcc;
    ImageView faceBtn;
    TabHost host;
    public String userName,userLink,userEmail,userBirthday,userid,userGender,userLocation,userAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        callbackManager=CallbackManager.Factory.create();
        tvName=(TextView)findViewById(R.id.tvName);
        tvAgeGender=(TextView)findViewById(R.id.tvAgeGender);
        tvEmail=(TextView)findViewById(R.id.tvEmailPhone);
        tvAddress=(TextView)findViewById(R.id.etAddress);
        tvBank=(TextView)findViewById(R.id.tvBank);
        profilePictureView=(ProfilePictureView)findViewById(R.id.imageUser);
        editBtn=(Button)findViewById(R.id.editBtn);
        faceBtn=(ImageView)findViewById(R.id.faceLink);
        host=(TabHost)findViewById(R.id.tabHost);
        host.setup();
        setUpTab();

        accessToken = AccessToken.getCurrentAccessToken();
        userid=accessToken.getUserId();

        user=new User();
        user.setUserID(userid);
        address=new Address();
        address.setUserID(userid);
        address.setStateID(-1);
        bankAcc=new BankAcc();
        bankAcc.setUserID(userid);

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                           // id=object.getString("id");
                            userName=object.getString("name");
                            userEmail=object.getString("email");
                            userGender=object.getString("gender");
                            userBirthday=object.getString("birthday");
                            userLink=object.getString("link");
                            userLocation=object.getString("location");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String ageGender="";
                        tvName.setText(userName);
                        if(userBirthday!=null){
                            ageGender=userBirthday+"\n";
                        }
                        if(userGender!=null){
                            ageGender=ageGender+userGender;
                        }
                        tvAgeGender.setText(ageGender);

                        profilePictureView.setProfileId(userid);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday,link,location");
        request.setParameters(parameters);
        request.executeAsync();

        try {
            facebookLink(userid);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        displayDetail();
        editProfile();
    }

    private void setUpTab() {
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Contact");
        spec.setContent(R.id.tab1);
        spec.setIndicator("",getResources().getDrawable(R.drawable.profile_contact));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Address");
        spec.setContent(R.id.tab2);
        spec.setIndicator("",getResources().getDrawable(R.drawable.profile_add));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Bank");
        spec.setContent(R.id.tab3);
        spec.setIndicator("",getResources().getDrawable(R.drawable.profile_bank));
        host.addTab(spec);
    }

    private void displayDetail() {
        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                String textEmail="";
                if(returnedUser.getEmail().length()>5){
                    textEmail=returnedUser.getEmail();
                }else{
                    textEmail="Please update your Email";
                }

                if(returnedUser.getPhone().length()>5){
                    textEmail=textEmail+"\n"+returnedUser.getPhone();
                }else{
                    textEmail=textEmail+"\n"+"Please update your Contact";
                }
                tvEmail.setText(textEmail);
            }
        });

        userRequest.fetchAddressDataInBackground(address, new GetAddressCallBack() {
            @Override
            public void done(Address address) {
                String state = "Please update your Shipping Address";
                int stateId = address.getStateID();
                if(stateId!=-1){
                state = type.get(stateId).toString();
                    tvAddress.setText(address.getAddress() + "\n" +
                            address.getPoskod() + "  " + address.getCity() + "\n" +
                            state);
                }else{
                    tvAddress.setText(state);
                }
            }
        });

        userRequest.fetchAllBankDataInBackground(bankAcc, new GetBankAccCallBack() {
            @Override
            public void done(ArrayList<BankAcc> bankAccsList) {
                String bankList = "";
                String bankName = null;
                int bankID = -1;
                for (int i = 0; i < bankAccsList.size(); i++) {
                    String name = bankAccsList.get(i).getName();
                    String accNo = bankAccsList.get(i).getAccUser();
                    bankID = bankAccsList.get(i).getBankID();
                    bankName = banktype.get(bankID).toString();

                    bankList = bankList + bankName + "  " + accNo + "\n" + name + "\n\n";
                }

                if (bankList.length() < 8) {
                    tvBank.setText("Please update your Bank Account");
                } else {
                    tvBank.setText(bankList);
                }
            }
        });
    }

    private void editProfile() {
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UpdateUserProfileActivity.class));
                finish();
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            startActivity(new Intent(ProfileActivity.this, UpdateUserProfileActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
