package com.example.jarry.persell;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.MessagingAdapter;
import com.example.jarry.persell.CallBack.GetMessageCallBack;
import com.example.jarry.persell.CallBack.GetMsgCallBack;
import com.example.jarry.persell.CallBack.GetRoomChatCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.MessageChat;
import com.example.jarry.persell.Entity.RoomChat;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Util.DateToString;
import com.example.jarry.persell.Util.MessageRequest;
import com.example.jarry.persell.Util.RoomRequest;
import com.example.jarry.persell.Util.UserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagingActivity extends BaseActivity implements MessageClientListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid;
    private String ownerid,ownerName,userName;
    private RoomChat roomChat;
    ArrayList<MessageChat> messageChats;
    private int roomID;
    MessageRequest messageRequest;
    RoomRequest roomRequest;
    ListView messagesList;

    private static final String TAG = MessagingActivity.class.getSimpleName();

    private MessagingAdapter adapter;
    private EditText mTxtTextBody;
    private Button mBtnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_messaging);

        callbackManager=CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(MessagingActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        User user=new User();
        user.setUserID(userid);
        UserRequest userRequest=new UserRequest(this);
        userRequest.fetchUserDataInBackground(user, new GetUserCallBack() {
            @Override
            public void done(User returnedUser) {
                userName=returnedUser.getUserName();
            }
        });

        ownerid=getIntent().getExtras().getString("ownerid");
        ownerName=getIntent().getExtras().getString("ownerName");
        messageChats=new ArrayList<>();

        roomChat=new RoomChat(null,ownerid,-1,userid);
        messageRequest=new MessageRequest(this);
        roomRequest=new RoomRequest(this);

        roomRequest.checkRoomDataInBackground(roomChat, new GetRoomChatCallBack() {
            @Override
            public void done(ArrayList<RoomChat> rooms) {
                roomID = rooms.get(0).getRoomID();
                getMessageData(rooms.get(0));
            }
        });

        messagesList = (ListView) findViewById(R.id.lstMessages);
        mTxtTextBody = (EditText) findViewById(R.id.txtTextBody);
        mBtnSend = (Button) findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void getMessageData(RoomChat room) {
        messageRequest.fetchMessageDataInBackground(room, new GetMessageCallBack() {
            @Override
            public void done(ArrayList<MessageChat> messages) {
                if(messages.size()!=0){
                messageChats=messages;
                adapter=new MessagingAdapter(MessagingActivity.this,userid,messageChats,userName,ownerName);
                messagesList.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onServiceConnected() {
        getSinchServiceInterface().addMessageClientListener(this);
        setButtonEnabled(true);
    }

    @Override
    public void onServiceDisconnected() {
        setButtonEnabled(false);
    }

    private void sendMessage() {
        String textBody = mTxtTextBody.getText().toString();
        if (ownerid.isEmpty()) {
            Toast.makeText(this, "No recipient added", Toast.LENGTH_SHORT).show();
            return;
        }
        if (textBody.isEmpty()) {
            Toast.makeText(this, "No text message", Toast.LENGTH_SHORT).show();
            return;
        }

        DateToString dateToString=new DateToString();
        getSinchServiceInterface().sendMessage(ownerid, textBody);
        MessageChat msg=new MessageChat(dateToString.date2String(),textBody,0,roomID,userid);
        messageRequest.storeMessageDataInBackground(msg, new GetMsgCallBack() {
            @Override
            public void done(MessageChat msg) {

            }
        });
        mTxtTextBody.setText("");
    }

    private void setButtonEnabled(boolean enabled) {
        mBtnSend.setEnabled(enabled);
    }

    @Override
    public void onIncomingMessage(MessageClient client, Message message) {
        DateToString dateToString=new DateToString();
        String text=message.getTextBody();
        String sendID=message.getSenderId();
        Date date=message.getTimestamp();
        String Sdate=dateToString.date2String2(date);

        setNotification(text);
        MessageChat msg=new MessageChat(Sdate,text,0,roomID,sendID);
        if(ownerid.equals(sendID))
        adapter.addMessage(msg,1);
    }

    private void setNotification(String text) {
        Intent intent = new Intent(this, MessagingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Incoming Message")
                .setContentText(text).setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);
    }

    @Override
    public void onMessageSent(MessageClient client, Message message, String recipientId) {
        DateToString dateToString=new DateToString();
        String text=message.getTextBody();
        String sendID=message.getSenderId();
        Date date=message.getTimestamp();
        String Sdate=dateToString.date2String2(date);

        MessageChat msg=new MessageChat(Sdate,text,0,roomID,sendID);
        adapter.addMessage(msg,0);
    }

    @Override
    public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {
        // Left blank intentionally
    }

    @Override
    public void onMessageFailed(MessageClient client, Message message,
                                MessageFailureInfo failureInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Sending failed: ")
                .append(failureInfo.getSinchError().getMessage());

        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, sb.toString());
    }

    @Override
    public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {
        Log.d(TAG, "onDelivered");
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
