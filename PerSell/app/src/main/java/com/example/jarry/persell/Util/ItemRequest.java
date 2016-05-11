package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.jarry.persell.CallBack.GetAllItemCallBack;
import com.example.jarry.persell.CallBack.GetBankAccCallBack;
import com.example.jarry.persell.CallBack.GetImageCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.BankAcc;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jarry on 25/2/2016.
 */
public class ItemRequest {
    ProgressDialog progressDialog;
    Context context;

    public ItemRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void storeItemDataInBackground(Item item,GetItemCallBack callBack){
        progressDialog.show();
        new StoreItemDataAsyncTask(callBack,item).execute();
    }

    public class StoreItemDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Item item;
        GetItemCallBack callBack;

        public StoreItemDataAsyncTask(GetItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",item.getUserID()));
            dataToSend.add(new BasicNameValuePair("categoryID", item.getCategoryID() + ""));
            dataToSend.add(new BasicNameValuePair("itemPrice",item.getItemPrice()+""));
            dataToSend.add(new BasicNameValuePair("pic1",item.getPicname1()));
            dataToSend.add(new BasicNameValuePair("pic2",item.getPicname2()));
            dataToSend.add(new BasicNameValuePair("pic3",item.getPicname3()));
            dataToSend.add(new BasicNameValuePair("itemDes",item.getItemDes()));
            dataToSend.add(new BasicNameValuePair("statusID",item.getStatusID()+""));
            dataToSend.add(new BasicNameValuePair("date",item.getDate()));
            dataToSend.add(new BasicNameValuePair("stateID",item.getStateID()+""));
            dataToSend.add(new BasicNameValuePair("itemTitle",item.getItemTitle()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertItem.php");

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

    public void fetchMyItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchMyItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchMyItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchMyItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", item.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchMyItemList.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");

                   items.add(new Item(itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void fetchSelectedItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchSelectedItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchSelectedItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchSelectedItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", item.getUserID()));
            dataToSend.add(new BasicNameValuePair("statusID", item.getStatusID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchSelectedItemList.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");

                    items.add(new Item(itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void deleteItemDataInBackground(Item item,GetItemCallBack callBack){
        progressDialog.show();
        new DeleteItemDataAsyncTask(callBack,item).execute();
    }

    public class DeleteItemDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Item item;
        GetItemCallBack callBack;

        public DeleteItemDataAsyncTask(GetItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",item.getItemID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"deleteItem.php");

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

    public void editItemDataInBackground(Item item,GetItemCallBack callBack){
        progressDialog.show();
        new EditItemDataAsyncTask(callBack,item).execute();
    }

    public class EditItemDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Item item;
        GetItemCallBack callBack;

        public EditItemDataAsyncTask(GetItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",item.getItemID()+""));
            dataToSend.add(new BasicNameValuePair("categoryID", item.getCategoryID() + ""));
            dataToSend.add(new BasicNameValuePair("itemPrice",item.getItemPrice()+""));
            dataToSend.add(new BasicNameValuePair("pic1",item.getPicname1()));
            dataToSend.add(new BasicNameValuePair("pic2",item.getPicname2()));
            dataToSend.add(new BasicNameValuePair("pic3",item.getPicname3()));
            dataToSend.add(new BasicNameValuePair("itemDes",item.getItemDes()));
            dataToSend.add(new BasicNameValuePair("stateID",item.getStateID()+""));
            dataToSend.add(new BasicNameValuePair("itemTitle",item.getItemTitle()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"updateItem.php");

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

    public void fetchItemDataInBackground(Item item, GetItemCallBack callBack){
        progressDialog.show();
        new fetchItemDataAsyncTask(callBack,item).execute();
    }

    public class fetchItemDataAsyncTask extends AsyncTask<Void,Void,Item>{

        Item item;
        GetItemCallBack callBack;

        public fetchItemDataAsyncTask(GetItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item = item;
        }

        @Override
        protected Item doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", item.getItemID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchItemData.php");

            Item returnedItem=null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();
                String result= EntityUtils.toString(entity);
                JSONObject jsonObject=new JSONObject(result);

                if(jsonObject.length()==0){
                    returnedItem=null;
                }else{
                    int itemID=jsonObject.getInt("itemID");
                    int categoryID=jsonObject.getInt("categoryID");
                    double itemPrice=jsonObject.getDouble("itemPrice");
                    String pic1=jsonObject.getString("pic1");
                    String pic2=jsonObject.getString("pic2");
                    String pic3=jsonObject.getString("pic3");
                    String itemDes=jsonObject.getString("itemDes");
                    int statusID=jsonObject.getInt("statusID");
                    String UserID=jsonObject.getString("UserID");
                    String date=jsonObject.getString("date");
                    int stateID=jsonObject.getInt("stateID");
                    String itemTitle=jsonObject.getString("itemTitle");

                    returnedItem=new Item(itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,UserID,date,categoryID);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return returnedItem;
    }

        @Override
        protected void onPostExecute(Item returnedItem) {

            progressDialog.dismiss();
            callBack.done(returnedItem);

            super.onPostExecute(returnedItem);
        }
    }

    public void fetchAvailableItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchAvailableItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchAvailableItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchAvailableItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("statusID", item.getStatusID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchAvailabeItemList.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");

                    items.add(new Item(itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void fetchSearchedItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchSearchedItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchSearchedItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchSearchedItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("sql", item.getItemTitle()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchSearchedItemList.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");
                    String username=c.getString("UserName");

                    items.add(new Item(username,itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void fetchPurchaseItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchPurchaseItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchPurchaseItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchPurchaseItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", item.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchPurchaseItemList.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");

                    items.add(new Item(itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void fetchSearchingItemsDataInBackground(Item item,GetAllItemCallBack callBack){
        progressDialog.show();
        new fetchSearchingItemsDataAsyncTask(callBack,item).execute();
    }

    public class fetchSearchingItemsDataAsyncTask extends AsyncTask<Void,Void,ArrayList<Item>>{

        Item item;
        ArrayList<Item> items=new ArrayList<>();
        GetAllItemCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchSearchingItemsDataAsyncTask(GetAllItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item=item;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("search", item.getItemTitle()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"search.php");

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
        protected void onPostExecute(ArrayList<Item> items) {
            progressDialog.dismiss();
            callBack.done(items);
            super.onPostExecute(items);
        }

        protected ArrayList<Item> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);

                    int itemID=c.getInt("itemID");
                    int categoryID=c.getInt("categoryID");
                    double itemPrice=c.getDouble("itemPrice");
                    String pic1=c.getString("pic1");
                    String pic2=c.getString("pic2");
                    String pic3=c.getString("pic3");
                    String itemDes=c.getString("itemDes");
                    int statusID=c.getInt("statusID");
                    String userid=c.getString("UserID");
                    String date=c.getString("date");
                    int stateID=c.getInt("stateID");
                    String itemTitle=c.getString("itemTitle");
                    String username=c.getString("UserName");

                    items.add(new Item(username,itemTitle,itemDes,itemID,itemPrice,pic1,pic2,pic3,stateID,statusID,userid,date,categoryID));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return items;
        }
    }

    public void cancelBookedDataInBackground(Item item,GetItemCallBack callBack){
        progressDialog.show();
        new CancelBookedDataAsyncTask(callBack,item).execute();
    }

    public class CancelBookedDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Item item;
        GetItemCallBack callBack;

        public CancelBookedDataAsyncTask(GetItemCallBack callBack, Item item) {
            this.callBack = callBack;
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",item.getItemID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"cancelBooked.php");

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
