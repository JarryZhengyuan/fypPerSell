package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.jarry.persell.CallBack.GetAllCommentCallBack;
import com.example.jarry.persell.CallBack.GetCommentCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.Entity.Comment;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.User;

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
 * Created by Jarry on 28/2/2016.
 */
public class CommentRequest {

    ProgressDialog progressDialog;
    Context context;

    public CommentRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void storeCommentDataInBackground(Comment comment,GetCommentCallBack callBack){
        new StoreCommentDataAsyncTask(callBack,comment).execute();
    }

    public class StoreCommentDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Comment comment;
        GetCommentCallBack callBack;

        public StoreCommentDataAsyncTask(GetCommentCallBack callBack, Comment comment) {
            this.callBack = callBack;
            this.comment = comment;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("content",comment.getContent()));
            dataToSend.add(new BasicNameValuePair("date", comment.getDate()));
            dataToSend.add(new BasicNameValuePair("itemID",comment.getItemID()+""));
            dataToSend.add(new BasicNameValuePair("UserID",comment.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertComment.php");

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

    public void fetchCommentDataInBackground(Comment comment,GetAllCommentCallBack callBack){
        progressDialog.show();
        new fetchCommentDataAsyncTask(callBack,comment).execute();
    }

    public class fetchCommentDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Comment>>{

        Comment comment;
        User user;
        ArrayList<Comment> comments=new ArrayList<>();
        GetAllCommentCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchCommentDataAsyncTask(GetAllCommentCallBack callBack, Comment comment) {
            this.callBack = callBack;
            this.comment=comment;
        }

        @Override
        protected ArrayList<Comment> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("itemID", comment.getItemID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchCommentList.php");

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
        protected void onPostExecute(ArrayList<Comment> comments) {
            progressDialog.dismiss();
            callBack.done(comments);
            super.onPostExecute(comments);
        }

        protected ArrayList<Comment> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int commentID=c.getInt("commentID");
                    String content=c.getString("content");
                    String date=c.getString("date");
                    int itemID=c.getInt("itemID");
                    String userid=c.getString("UserID");
                    String username=c.getString("UserName");

                    user=new User();
                    user.setUserName(username);
                    comments.add(new Comment(user,commentID,content,date,itemID,userid));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return comments;
        }
    }

    public void deleteCommentDataInBackground(Comment comment,GetCommentCallBack callBack){
        progressDialog.show();
        new DeleteCommentDataAsyncTask(callBack,comment).execute();
    }

    public class DeleteCommentDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Comment comment;
        GetCommentCallBack callBack;

        public DeleteCommentDataAsyncTask(GetCommentCallBack callBack, Comment comment) {
            this.callBack = callBack;
            this.comment = comment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",comment.getCommentID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"deleteComment.php");

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

            progressDialog.dismiss();
            callBack.done(null);

            super.onPostExecute(aVoid);
        }
    }

    public void updateCommentDataInBackground(Comment comment,GetCommentCallBack callBack){
        progressDialog.show();
        new UpdateCommentDataAsyncTask(callBack,comment).execute();
    }

    public class UpdateCommentDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Comment comment;
        GetCommentCallBack callBack;

        public UpdateCommentDataAsyncTask(GetCommentCallBack callBack, Comment comment) {
            this.callBack = callBack;
            this.comment = comment;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("commentID",comment.getCommentID()+""));
            dataToSend.add(new BasicNameValuePair("content",comment.getContent()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"updateComment.php");

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

            progressDialog.dismiss();
            callBack.done(null);

            super.onPostExecute(aVoid);
        }
    }

}
