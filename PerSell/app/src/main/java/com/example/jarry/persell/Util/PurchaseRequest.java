package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.jarry.persell.CallBack.GetInvoiceCallBack;
import com.example.jarry.persell.CallBack.GetItemCallBack;
import com.example.jarry.persell.CallBack.GetMsgCallBack;
import com.example.jarry.persell.CallBack.GetPictureCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.Invoice;
import com.example.jarry.persell.Entity.Item;
import com.example.jarry.persell.Entity.MessageChat;

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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Jarry on 6/3/2016.
 */
public class PurchaseRequest {

    Context context;
    ProgressDialog progressDialog;

    public PurchaseRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void storeInvoiceDataInBackground(Invoice invoice,GetInvoiceCallBack callBack){
        new StoreInvoiceDataAsyncTask(callBack,invoice).execute();
    }

    public class StoreInvoiceDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Invoice invoice;
        GetInvoiceCallBack callBack;

        public StoreInvoiceDataAsyncTask(GetInvoiceCallBack callBack, Invoice invoice) {
            this.callBack = callBack;
            this.invoice = invoice;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("poskod",invoice.getAdd().getPoskod()));
            dataToSend.add(new BasicNameValuePair("city", invoice.getAdd().getCity()));
            dataToSend.add(new BasicNameValuePair("stateID",invoice.getAdd().getStateID()+""));
            dataToSend.add(new BasicNameValuePair("address",invoice.getAdd().getAddress()));
            dataToSend.add(new BasicNameValuePair("itemID", invoice.getItemID()+""));
            dataToSend.add(new BasicNameValuePair("date",invoice.getDate()));
            dataToSend.add(new BasicNameValuePair("UserID",invoice.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertShipAdd.php");

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

    public void updateInvoiceDataInBackground(Invoice invoice,GetInvoiceCallBack callBack){
        new UpdateInvoiceDataAsyncTask(callBack,invoice).execute();
    }

    public class UpdateInvoiceDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Bitmap bitmap;
        Invoice invoice;
        GetInvoiceCallBack callBack;

        public UpdateInvoiceDataAsyncTask(GetInvoiceCallBack callBack, Invoice invoice) {
            this.callBack = callBack;
            this.invoice = invoice;
            bitmap=invoice.getPayPic();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("payPic",invoice.getPicname()));
            dataToSend.add(new BasicNameValuePair("image", encodedImage));
            dataToSend.add(new BasicNameValuePair("itemID", invoice.getItemID()+""));
            dataToSend.add(new BasicNameValuePair("date",invoice.getDate()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"saveInvoicePicture.php");

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

    public void downloadPictureData(Bitmap bitmap,String name,GetPictureCallBack callBack){
        progressDialog.show();
        new DownloadImageData(bitmap,name,callBack).execute();
    }

    public class DownloadImageData extends AsyncTask<Void, Void,Bitmap> {

        Bitmap bitmap;
        String name;
        GetPictureCallBack callBack;

        public DownloadImageData(Bitmap bitmap,String name,GetPictureCallBack callBack){
            this.bitmap=bitmap;
            this.name=name;
            this.callBack=callBack;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            String url=ServerRequest.SERVER_ADDRESS+"invoice/"+name+".JPG";

            try {
                URLConnection connection=new URL(url).openConnection();
                connection.setConnectTimeout(ServerRequest.CONNECTION_TIME);
                connection.setReadTimeout(ServerRequest.CONNECTION_TIME);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            if(bitmap!=null){
                callBack.done(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }

    public void fetchInvoiceDataInBackground(Invoice invoice, GetInvoiceCallBack callBack){
        progressDialog.show();
        new fetchInvoiceDataAsyncTask(callBack,invoice).execute();
    }

    public class fetchInvoiceDataAsyncTask extends AsyncTask<Void,Void,Invoice>{

        Invoice invoice;
        GetInvoiceCallBack callBack;

        public fetchInvoiceDataAsyncTask(GetInvoiceCallBack callBack, Invoice invoice) {
            this.callBack = callBack;
            this.invoice = invoice;
        }

        @Override
        protected Invoice doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", invoice.getItemID() + ""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME_LARGE);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchPurchaseItem.php");

            Invoice returnedInvoice=null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();
                String result= EntityUtils.toString(entity);
                JSONObject jsonObject=new JSONObject(result);

                if(jsonObject.length()==0){
                    returnedInvoice=null;
                }else{

                    String poskod=jsonObject.getString("poskod");
                    String city=jsonObject.getString("city");
                    int stateID=jsonObject.getInt("stateID");
                    String address=jsonObject.getString("address");
                    int itemID=jsonObject.getInt("itemID");
                    String date=jsonObject.getString("date");
                    String payPic=jsonObject.getString("payPic");
                    int status=jsonObject.getInt("status");
                    String UserID=jsonObject.getString("UserID");

                    Log.d("Address", address);

                    Address add=new Address(address,city,poskod,stateID,null);
                    returnedInvoice=new Invoice(UserID,add,date,itemID,null,payPic,status);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return returnedInvoice;
        }

        @Override
        protected void onPostExecute(Invoice returnedInvoice) {

            progressDialog.dismiss();
            callBack.done(returnedInvoice);

            super.onPostExecute(returnedInvoice);
        }
    }
}
