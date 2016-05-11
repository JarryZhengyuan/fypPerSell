package com.example.jarry.persell;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jarry.persell.Util.ConnectionDetector;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class FaceLoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AccessToken accessToken;
    public AccessToken token;
    public Profile profile;
    ConnectionDetector cd;
    Boolean isInternetPresent;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            token = loginResult.getAccessToken();
            profile = Profile.getCurrentProfile();
            startActivityForResult(new Intent(FaceLoginActivity.this, LoginSinchActivity.class), 0);
        }
        @Override
        public void onCancel() {
            messsageToast("Login attempt canceled.");
        }
        @Override
        public void onError(FacebookException e) {
            messsageToast("Login attempt failed.");
        }
    };

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_face_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        isInternetPresent = cd.isConnectingToInternet();

        if (isInternetPresent) {
            accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                startActivity(new Intent(FaceLoginActivity.this,LoginSinchActivity.class));
                finish();
            }
        } else {
            showAlertDialog(FaceLoginActivity.this, "No Internet Connection",
                    "You don't have internet connection.", false);
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        loginButton.registerCallback(callbackManager, mCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            this.finish();
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
