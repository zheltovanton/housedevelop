package com.housedevelop.housedevelop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.impl.client.DefaultHttpClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import java.net.URL;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.UnsupportedEncodingException;
import android.util.Base64;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import  	java.io.FileInputStream;
import  	java.io.File;
import  	java.io.OutputStream;
import  	java.io.FileOutputStream;
import  	java.io.FileNotFoundException;
import com.housedevelop.housedevelop.FileCache;
import 	android.os.AsyncTask;
import android.view.View.OnClickListener;

public class FavViewActivity extends ActionBarActivity  {

    String User_uid;
    List<View> favlist = new ArrayList<View>();
    LayoutInflater inflater;
    private ProgressDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);
        Bundle bundle = getIntent().getParcelableExtra("bundle");

        User_uid = bundle.getString("User_uid");
        inflater = getLayoutInflater();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.loading));
        showDialog();
        LoadFavs();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fav_view_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fav_action_refresh) {
            LoadFavs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // --------------------------------------------------------------------------------------------

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    // --------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        LoadFavs();

    }

    // --------------------------------------------------------------------------------------------

    private class HttpAsyncTask_Favs extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            ///  Load favorite list

            try {
                hideDialog();
                // catch json with comments from server
                JSONArray jObj = new JSONArray(result);
                favlist.clear();
                //Log.e("favs ",jObj.toString());
                LinearLayout linLayout = (LinearLayout) findViewById(R.id.fav_list);
                linLayout.removeAllViews();

                // make comments for every record from db
                for (int i = 0; i < jObj.length(); i++) {
                    JSONObject jsonObj = jObj.getJSONObject(i);
                    // tvhvDeveloper.setText(tvhvDeveloper.getText()+jsonObj.getString("filename"));
                    Log.d("json fav",jsonObj.toString());

                    favlist.add(inflater.inflate(R.layout.fav_list_item, null, false));

                    linLayout.addView(favlist.get(i));

                    View item = favlist.get(i);
                    item.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            TextView id = (TextView) view.findViewById(R.id.fav_ls_id);
                            String sid = id.getText().toString();
                            Bundle b=new Bundle();
                            b.putString("User_uid",User_uid);
                            b.putString("id",sid);

                            Intent i = new Intent(FavViewActivity.this, Houseview.class);
                            i.putExtra("bundle", b);
                            startActivity(i);

                        }

                    });

                    // parse json result

                    // set housedetail info
                    TextView comment = (TextView) item.findViewById(R.id.fav_ls_comment);
                    String s = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("housedetails")));
                    comment.setText(s);
                    if (s.isEmpty()) {
                        comment.setVisibility(View.GONE);
                    }

                    // set address info
                    TextView address = (TextView) item.findViewById(R.id.fav_ls_address);
                    String s1 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("address")));
                    String s2 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("city")));
                    if (!s2.isEmpty()) {
                        s1=s1+", "+s2;
                    }
                    address.setText(s1);

                    // set username info
                    TextView username = (TextView) item.findViewById(R.id.fav_ls_username);
                    String s3 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("username")));
                    username.setText(s3);

                    // set developer info
                    TextView developer = (TextView) item.findViewById(R.id.fav_ls_developer);
                    String s4 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("developer")));
                    String s5 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("developer_desc")));
                    if (!s5.isEmpty()) {
                        s4=s4+": "+s5;
                    }
                    developer.setText(s4);
                    if (s4.isEmpty()) {
                        developer.setVisibility(View.GONE);
                    }

                    // set date create info
                    TextView date = (TextView) item.findViewById(R.id.fav_ls_date);
                    String s6 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("created_at")));
                    date.setText(s6);

                    // set date ID hidden info
                    TextView id = (TextView) item.findViewById(R.id.fav_ls_id);
                    String s7 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("fav_house_id")));
                    id.setText(s7);

                    // set date LastComment hidden info
                    TextView lastcomment = (TextView) item.findViewById(R.id.fav_ls_lastcomment);
                    String s8 = AppConfig.myFormat(java.net.URLDecoder.decode(jsonObj.getString("lastcomment")));
                    lastcomment.setText(s8);
                    if (s8.isEmpty() || s8.equals("null")) {
                        comment.setVisibility(View.GONE);
                    }
/*
                    TextView username = (TextView) item.findViewById(R.id.ls_username);
                    username.setText(jsonObj.getString("username"));

                    TextView date = (TextView) item.findViewById(R.id.ls_date);
                    date.setText(jsonObj.getString("date"));
*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    public void LoadFavs(){
        // check if you are connected or not
        String tag_string_req = "fav_getlist";
        if(isConnected()){
            new HttpAsyncTask_Favs().execute(AppConfig.URL_FAVGET+"?tag="+tag_string_req+"&user_id="+User_uid);
        }
        else{

        }
    }
    //----------------------------------------------------------------------------------------------

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //----------------------------------------------------------------------------------------------

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //----------------------------------------------------------------------------------------------

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    //----------------------------------------------------------------------------------------------

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
