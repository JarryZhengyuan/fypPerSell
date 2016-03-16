package com.example.jarry.persell.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.jarry.persell.CallBack.GetAddressCallBack;
import com.example.jarry.persell.CallBack.GetBankAccCallBack;
import com.example.jarry.persell.CallBack.GetUserCallBack;
import com.example.jarry.persell.Entity.Address;
import com.example.jarry.persell.Entity.BankAcc;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Jarry on 22/2/2016.
 */
public class UserRequest {
    ProgressDialog progressDialog;
    Context context;

    public UserRequest(Context context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        this.context=context;
    }

    public void storeUserDataInBackground(User user,GetUserCallBack callBack){
        progressDialog.show();
        new StoreUserDataAsyncTask(callBack,user).execute();
    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void,Void> {

        User user;
        GetUserCallBack callBack;

        public StoreUserDataAsyncTask(GetUserCallBack callBack, User user) {
            this.callBack = callBack;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",user.getUserID()));
            dataToSend.add(new BasicNameValuePair("UserName",user.getUserName()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"login.php");

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

    public void updateUserDataInBackground(User user,GetUserCallBack callBack){
        progressDialog.show();
        new UpdateUserDataAsyncTask(callBack,user).execute();
    }

    public class UpdateUserDataAsyncTask extends AsyncTask<Void,Void,Void> {

        User user;
        GetUserCallBack callBack;

        public UpdateUserDataAsyncTask(GetUserCallBack callBack, User user) {
            this.callBack = callBack;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",user.getUserID()));
            dataToSend.add(new BasicNameValuePair("email",user.getEmail()));
            dataToSend.add(new BasicNameValuePair("phone",user.getPhone()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"updateEmailPhone.php");

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

    public void storeUserAddressDataInBackground(Address address,GetUserCallBack callBack){
        progressDialog.show();
        new StoreUserAddressDataAsyncTask(callBack,address).execute();
    }

    public class StoreUserAddressDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Address address;
        GetUserCallBack callBack;

        public StoreUserAddressDataAsyncTask(GetUserCallBack callBack, Address address) {
            this.callBack = callBack;
            this.address =address;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",address.getUserID()));
            dataToSend.add(new BasicNameValuePair("stateID",address.getStateID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertAddress.php");

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

    public void updateUserAddressDataInBackground(Address address,GetUserCallBack callBack){
        progressDialog.show();
        new UpdateUserAddressDataAsyncTask(callBack,address).execute();
    }

    public class UpdateUserAddressDataAsyncTask extends AsyncTask<Void,Void,Void> {

        Address address;
        GetUserCallBack callBack;

        public UpdateUserAddressDataAsyncTask(GetUserCallBack callBack, Address address) {
            this.callBack = callBack;
            this.address =address;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",address.getUserID()));
            dataToSend.add(new BasicNameValuePair("poskod",address.getPoskod()));
            dataToSend.add(new BasicNameValuePair("city",address.getCity()));
            dataToSend.add(new BasicNameValuePair("address",address.getAddress()));
            dataToSend.add(new BasicNameValuePair("stateID",address.getStateID()+""));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"updateAddress.php");

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

    public void storeUserBankDataInBackground(BankAcc bankAcc,GetUserCallBack callBack){
        progressDialog.show();
        new StoreUserBankDataAsyncTask(callBack,bankAcc).execute();
    }

    public class StoreUserBankDataAsyncTask extends AsyncTask<Void,Void,Void> {

        BankAcc bankAcc;
        GetUserCallBack callBack;

        public StoreUserBankDataAsyncTask(GetUserCallBack callBack, BankAcc bankAcc) {
            this.callBack = callBack;
            this.bankAcc =bankAcc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",bankAcc.getUserID()));
            dataToSend.add(new BasicNameValuePair("accUser",bankAcc.getAccUser()));
            dataToSend.add(new BasicNameValuePair("bankID",bankAcc.getBankID()+""));
            dataToSend.add(new BasicNameValuePair("name",bankAcc.getName()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"insertBankAcc.php");

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

    public void fetchAllBankDataInBackground(BankAcc bankAcc, GetBankAccCallBack callBack){
        progressDialog.show();
        new fetchAllBankDataAsyncTask(callBack,bankAcc).execute();
    }

    public class fetchAllBankDataAsyncTask extends AsyncTask<Void,Void,ArrayList<BankAcc>>{

        BankAcc bankAcc;
        ArrayList<BankAcc> bankAccList=new ArrayList<>();
        GetBankAccCallBack callBack;
        String myJSON;
        JSONArray peoples = null;

        public fetchAllBankDataAsyncTask(GetBankAccCallBack callBack, BankAcc bankAcc) {
            this.callBack = callBack;
            this.bankAcc=bankAcc;
        }

        @Override
        protected ArrayList<BankAcc> doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id", bankAcc.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client = new DefaultHttpClient(httpParams);
            HttpPost post = new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchBankAcc.php");

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
        protected void onPostExecute(ArrayList<BankAcc> bankAccList) {
            progressDialog.dismiss();
            callBack.done(bankAccList);
            super.onPostExecute(bankAccList);
        }

        protected ArrayList<BankAcc> showlist(String myJSON)  {
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(myJSON);
                peoples = jsonObj.getJSONArray("result");

                for(int i=0;i<peoples.length();i++){
                    JSONObject c = peoples.getJSONObject(i);
                    String acc = c.getString("accUser");
                    int bankID = c.getInt("bankID");
                    String userid = c.getString("UserID");
                    String name = c.getString("name");

                    bankAccList.add(new BankAcc(acc,bankID,name,userid));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return bankAccList;
        }
    }

    public void deleteBankDataInBackground(BankAcc bankAcc,GetUserCallBack callBack){
        progressDialog.show();
        new DeleteBankDataAsyncTask(callBack,bankAcc).execute();
    }

    public class DeleteBankDataAsyncTask extends AsyncTask<Void,Void,Void> {

        BankAcc bankAcc;
        GetUserCallBack callBack;

        public DeleteBankDataAsyncTask(GetUserCallBack callBack, BankAcc bankAcc) {
            this.callBack = callBack;
            this.bankAcc = bankAcc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",bankAcc.getUserID()));
            dataToSend.add(new BasicNameValuePair("accUser", bankAcc.getAccUser()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams,ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"deleteBankData.php");

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

    public void fetchUserDataInBackground(User user, GetUserCallBack callBack){
        progressDialog.show();
        new fetchUserDataAsyncTask(callBack,user).execute();
    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void,Void,User>{

        User user;
        GetUserCallBack callBack;

        public fetchUserDataAsyncTask(GetUserCallBack callBack, User user) {
            this.callBack = callBack;
            this.user = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",user.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchUserData.php");

            User returnedUser=null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();
                String result= EntityUtils.toString(entity);
                JSONObject jsonObject=new JSONObject(result);

                if(jsonObject.length()==0){
                    returnedUser=null;
                }else{
                    String userid=jsonObject.getString("UserID");
                    String phone=jsonObject.getString("phone");
                    String email=jsonObject.getString("email");
                    String username=jsonObject.getString("UserName");

                    returnedUser=new User(username,email,phone,userid);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {

            progressDialog.dismiss();
            callBack.done(returnedUser);

            super.onPostExecute(returnedUser);
        }
    }

    public void fetchAddressDataInBackground(Address address, GetAddressCallBack callBack){
        progressDialog.show();
        new fetchAddressDataAsyncTask(callBack,address).execute();
    }

    public class fetchAddressDataAsyncTask extends AsyncTask<Void,Void,Address>{

        Address address;
        GetAddressCallBack callBack;

        public fetchAddressDataAsyncTask(GetAddressCallBack callBack, Address address) {
            this.callBack = callBack;
            this.address = address;
        }

        @Override
        protected Address doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend=new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("id",address.getUserID()));

            HttpParams httpParams=new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, ServerRequest.CONNECTION_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, ServerRequest.CONNECTION_TIME);

            HttpClient client=new DefaultHttpClient(httpParams);
            HttpPost post=new HttpPost(ServerRequest.SERVER_ADDRESS+"fetchAddressData.php");

            Address returnedAdd=null;
            try{
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse=client.execute(post);

                HttpEntity entity=httpResponse.getEntity();
                String result= EntityUtils.toString(entity);
                JSONObject jsonObject=new JSONObject(result);

                if(jsonObject.length()==0){
                    returnedAdd=null;
                }else{
                    String poskod=jsonObject.getString("poskod");
                    String city=jsonObject.getString("city");
                    int stateID=jsonObject.getInt("stateID");
                    String address=jsonObject.getString("address");
                    String userid=jsonObject.getString("UserID");

                    returnedAdd=new Address(address,city,poskod,stateID,userid);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return returnedAdd;
        }

        @Override
        protected void onPostExecute(Address returnedAdd) {

            progressDialog.dismiss();
            callBack.done(returnedAdd);
            super.onPostExecute(returnedAdd);
        }
    }
}
