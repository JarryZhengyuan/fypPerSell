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
import android.widget.Toast;

import com.example.jarry.persell.Adapter.CommentAdapter;
import com.example.jarry.persell.CallBack.GetAllCommentCallBack;
import com.example.jarry.persell.CallBack.GetCommentCallBack;
import com.example.jarry.persell.Entity.Comment;
import com.example.jarry.persell.Entity.User;
import com.example.jarry.persell.Util.CommentRequest;
import com.example.jarry.persell.Util.DateToString;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    ProfilePictureView profilePictureView;
    private ListView list;
    private CommentAdapter adapter;
    private Comment comment;
    String[] values;
    User user;
    Dialog dialog;
    private EditText txtTextBody;
    private Button btnSend;
    ActionBar actionBar;
    private int item_id=-1;
    private String userid,item_title;
    CommentRequest commentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_comment);

        item_title=getIntent().getExtras().getString("item_name");
        actionBar = getSupportActionBar();
        actionBar.setTitle(item_title);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        callbackManager=CallbackManager.Factory.create();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            startActivity(new Intent(CommentActivity.this,FaceLoginActivity.class));
            finish();
        }
        userid=accessToken.getUserId();
        item_id=getIntent().getExtras().getInt("item_id");

        commentRequest=new CommentRequest(this);
        list=(ListView)findViewById(R.id.list_comment);
        txtTextBody=(EditText)findViewById(R.id.txtTextBody);
        btnSend=(Button)findViewById(R.id.btnEdit);

        btnSend.setOnClickListener(this);

        refreshList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEdit: sendMessage();
                break;
        }
    }

    private void sendMessage() {
        DateToString dateToString=new DateToString();
        if(txtTextBody.getText().toString().length()>0){
            Comment comment=new Comment(0,txtTextBody.getText().toString(),dateToString.date2String(),item_id,userid);
            commentRequest.storeCommentDataInBackground(comment, new GetCommentCallBack() {
                @Override
                public void done(Comment comment) {
                    refreshList();
                }
            });
        }
    }

    private void refreshList() {
        Comment comment=new Comment();
        comment.setItemID(item_id);
        commentRequest.fetchCommentDataInBackground(comment, new GetAllCommentCallBack() {
            @Override
            public void done(ArrayList<Comment> comments) {
                getCommentData(comments);
            }
        });
    }

    private void getCommentData(ArrayList<Comment> comments) {
        adapter= new CommentAdapter(getApplicationContext(), R.layout.comment_layout,comments);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                comment = adapter.getItem(position);
                if (comment.getUserID().equals(userid)) {
                    createDialog(comment);
                }
            }
        });
        txtTextBody.setText("");
    }

    private void createDialog(final Comment comment) {
        final CharSequence[] select = { "Edit", "Delete", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
        builder.setTitle(item_title);
        builder.setItems(select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (select[i].equals("Edit")) {
                    createEditDialog(comment);
                } else if (select[i].equals("Delete")) {
                    commentRequest.deleteCommentDataInBackground(comment, new GetCommentCallBack() {
                        @Override
                        public void done(Comment comment) {
                            refreshList();
                        }
                    });
                } else if (select[i].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void createEditDialog(final Comment comment) {
        dialog = new Dialog(CommentActivity.this);
        dialog.setContentView(R.layout.edit_comment);
        dialog.setTitle("Edit Content");
        dialog.setCancelable(true);

        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        editText.setText(comment.getContent());

        Button btnEdit=(Button)dialog.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setContent(editText.getText().toString());
                commentRequest.updateCommentDataInBackground(comment, new GetCommentCallBack() {
                    @Override
                    public void done(Comment comment) {
                        refreshList();
                    }
                });
                refreshList();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==android.R.id.home){
            Intent intent = new Intent(CommentActivity.this, ItemOwnerActivity.class);
            intent.putExtra("item_id", item_id);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void messsageToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
