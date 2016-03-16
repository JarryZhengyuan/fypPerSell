package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetMessageCallBack;
import com.example.jarry.persell.CallBack.GetMsgCallBack;
import com.example.jarry.persell.CallBack.GetRoomChatCallBack;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.MessageChat;
import com.example.jarry.persell.Entity.RoomChat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jarry on 3/3/2016.
 */
public class MessageRequest {

    Context context;
    ProgressDialog progressDialog;

    public MessageRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void storeMessageDataInBackground(MessageChat messageChat,GetMsgCallBack callBack){
        new StoreMessageDataAsyncTask(callBack,messageChat).execute();
    }

    public class StoreMessageDataAsyncTask extends AsyncTask<Void,Void,Void> {

        MessageChat messageChat;
        GetMsgCallBack callBack;

        public StoreMessageDataAsyncTask(GetMsgCallBack callBack, MessageChat messageChat) {
            this.callBack = callBack;
            this.messageChat = messageChat;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("roomID",messageChat.getRoomID()+""));
            dataToSend.add(new BasicNameValuePair("date", messageChat.getDate()));
            dataToSend.add(new BasicNameValuePair("message",messageChat.getMessage()));
            dataToSend.add(new BasicNameValuePair("UserID",messageChat.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertMessage.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callBack.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public void updateRoomChatDataInBackground(RoomChat room,GetMsgCallBack callBack){
        new UpdateRoomChatDataAsyncTask(callBack,room).execute();
    }

    public class UpdateRoomChatDataAsyncTask extends AsyncTask<Void,Void,Void> {

        RoomChat room;
        GetMsgCallBack callBack;

        public UpdateRoomChatDataAsyncTask(GetMsgCallBack callBack, RoomChat room) {
            this.callBack = callBack;
            this.room = room;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("roomID",room.getRoomID()+""));
            dataToSend.add(new BasicNameValuePair("date", room.getDate()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"updateRoomChat.php");

            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callBack.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public void fetchMessageDataInBackground(RoomChat roomChat,GetMessageCallBack callBack){
        progressDialog.show();
        new fetchMessageDataAsyncTask(callBack,roomChat).execute();
    }

    public class fetchMessageDataAsyncTask extends AsyncTask<Void,Void,ArrayList<MessageChat>> {

        RoomChat roomChat;
        ArrayList<MessageChat> msgs=new ArrayList<>();
        GetMessageCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchMessageDataAsyncTask(GetMessageCallBack callBack, RoomChat roomChat) {
            this.callBack = callBack;
            this.roomChat=roomChat;
        }

        @Override
        protected ArrayList<MessageChat> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("roomID", roomChat.getRoomID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchMessage.php");

            InputStream inputStream = null;
            String result = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();

                inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                // Oops
            }
            finally {
                try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
            }
            myJSON=result;
            return showlist(myJSON);
        }

        @Override
        protected void onPostExecute(ArrayList<MessageChat> msgs) {
            progressDialog.dismiss();
            callBack.done(msgs);
            super.onPostExecute(msgs);
        }

        protected ArrayList<MessageChat> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int messageID=c.getInt("messageID");
                    int roomID=c.getInt("roomID");
                    String date=c.getString("date");
                    String message=c.getString("message");
                    String UserID=c.getString("UserID");

                    msgs.add(new MessageChat(date,message,messageID,roomID,UserID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return msgs;
        }
    }
}
