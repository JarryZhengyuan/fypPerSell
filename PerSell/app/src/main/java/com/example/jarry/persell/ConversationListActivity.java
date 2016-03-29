package com.example.jarry.persell;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jarry.persell.Adapter.RoomChatAdapter;
import com.example.jarry.persell.CallBack.GetRoomChatCallBack;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.RoomChat;
import com.example.jarry.persell.Util.RoomRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

public class ConversationListActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private String userid;
    private ListView list;
    private SearchView searchView;
    RoomChat roomChat;
    RoomChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_conversation_list);

        callbackManager=CallbackManager.Factory.create();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Conversations");

        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(ConversationListActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();

        searchView=(SearchView)findViewById(R.id.sv);
        list=(ListView)findViewById(R.id.conList);

        roomChat=new RoomChat();
        refreshList();
    }

    private void refreshList() {
        roomChat.setOwnerID(userid);
        RoomRequest roomRequest=new RoomRequest(this);
        roomRequest.fetchRoomDataInBackground(roomChat, new GetRoomChatCallBack() {
            @Override
            public void done(ArrayList<RoomChat> rooms) {
                getRoomData(rooms);
            }
        });

    }

    private void getRoomData(ArrayList<RoomChat> rooms) {
        adapter=new RoomChatAdapter(getApplicationContext(),R.layout.room_chat,rooms);
        list.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomChat = adapter.getItem(position);
                 Intent intent = new Intent(ConversationListActivity.this, MessagingActivity.class);
                intent.putExtra("ownerid", roomChat.getOwnerID());
                intent.putExtra("ownerName", roomChat.getSenderID());
                 startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(ConversationListActivity.this,HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
