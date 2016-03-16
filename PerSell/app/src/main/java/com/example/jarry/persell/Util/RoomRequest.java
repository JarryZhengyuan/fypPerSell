package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.CallBack.GetRoomChatCallBack;
import com.example.jarry.persell.Entity.Item;
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
 * Created by Jarry on 5/3/2016.
 */
public class RoomRequest {
    ProgressDialog progressDialog;
    Context context;

    public RoomRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void fetchRoomDataInBackground(RoomChat roomChat,GetRoomChatCallBack callBack){
        progressDialog.show();
        new fetchRoomDataAsyncTask(callBack,roomChat).execute();
    }

    public class fetchRoomDataAsyncTask extends AsyncTask<Void,Void,ArrayList<RoomChat>> {

        RoomChat roomChat;
        ArrayList<RoomChat> rooms=new ArrayList<>();
        GetRoomChatCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchRoomDataAsyncTask(GetRoomChatCallBack callBack, RoomChat roomChat) {
            this.callBack = callBack;
            this.roomChat=roomChat;
        }

        @Override
        protected ArrayList<RoomChat> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("user", roomChat.getOwnerID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"checkConversationList.php");

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
        protected void onPostExecute(ArrayList<RoomChat> rooms) {
            progressDialog.dismiss();
            callBack.done(rooms);
            super.onPostExecute(rooms);
        }

        protected ArrayList<RoomChat> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int roomID=c.getInt("roomID");
                    String UserID=c.getString("UserID");
                    String UserName=c.getString("UserName");

                    rooms.add(new RoomChat(null,UserID,roomID,UserName));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return rooms;
        }
    }

    public void checkRoomDataInBackground(RoomChat roomChat,GetRoomChatCallBack callBack){
        progressDialog.show();
        new checkRoomDataAsyncTask(callBack,roomChat).execute();
    }

    public class checkRoomDataAsyncTask extends AsyncTask<Void,Void,ArrayList<RoomChat>> {

        RoomChat roomChat;
        ArrayList<RoomChat> rooms=new ArrayList<>();
        GetRoomChatCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public checkRoomDataAsyncTask(GetRoomChatCallBack callBack, RoomChat roomChat) {
            this.callBack = callBack;
            this.roomChat=roomChat;
        }

        @Override
        protected ArrayList<RoomChat> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("user_one", roomChat.getOwnerID()));
            dataToSend.add(new BasicNameValuePair("user_two", roomChat.getSenderID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"checkRoom.php");

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
        protected void onPostExecute(ArrayList<RoomChat> rooms) {
            progressDialog.dismiss();
            callBack.done(rooms);
            super.onPostExecute(rooms);
        }

        protected ArrayList<RoomChat> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int roomID=c.getInt("roomID");
                    String OwnerID=c.getString("user_one");
                    String SenderID=c.getString("user_two");
                    String date=c.getString("date");

                    rooms.add(new RoomChat(date,OwnerID,roomID,SenderID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return rooms;
        }
    }
}
