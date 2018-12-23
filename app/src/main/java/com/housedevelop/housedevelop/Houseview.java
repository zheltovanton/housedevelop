package com.housedevelop.housedevelop;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import java.net.URL;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.app.ProgressDialog;
import android.view.View.OnClickListener;

public class Houseview extends ActionBarActivity  {
    String User_uid;
    String marker_id;
    TextView tvhvUsername;
    TextView tvhvAddress;
    TextView tvhvHouseInfo;
    TextView tvhvDeveloper;
    TextView tvhvDate;
    TextView tvhvStage;
    TextView tvhvId;
    Button bthvEdit;
    LayoutInflater inflater;
    Boolean favstate = false;
    private Menu mmenu;
    private ProgressDialog pDialog;
    List<View> comments = new ArrayList<View>();
    Boolean loadinfo = false;
    Boolean loadfav = false;
    Boolean loadcomments = false;
    public static String[] ArrayHuman;
    public static String[] ArrayDB;

    //----------------------------------------------------------------------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            LoadHouseComments(marker_id);
            LoadHouseInfo(marker_id);
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houseview2);

        ArrayDB = getResources().getStringArray(R.array.addhouse_stage_build_values);
        ArrayHuman = getResources().getStringArray(R.array.addhouse_stage_build_titles);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        User_uid = bundle.getString("User_uid");
        marker_id = bundle.getString("id");
        inflater = getLayoutInflater();

        tvhvUsername = (TextView) findViewById(R.id.hw_creator_username);
        tvhvAddress = (TextView) findViewById(R.id.hw_txt_address);
        tvhvHouseInfo = (TextView) findViewById(R.id.hw_txt_housedetails);
        tvhvDeveloper = (TextView) findViewById(R.id.hw_txt_developer);
        tvhvDate = (TextView) findViewById(R.id.hw_txt_date);
        tvhvStage = (TextView) findViewById(R.id.hw_txt_stage);
        tvhvId = (TextView) findViewById(R.id.hw_txt_id);
        bthvEdit = (Button) findViewById(R.id.hw_bt_edit);
        bthvEdit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putString("User_uid",User_uid);
                b.putString("id",marker_id);

                Intent i = new Intent(Houseview.this, HouseEdit.class);
                i.putExtra("bundle", b);
                startActivity(i);
            }
        });
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.loading));
        showDialog();

        LoadHouseInfo(marker_id);
        LoadHouseComments(marker_id);
        LoadHouseFavState(marker_id);

    }


    //----------------------------------------------------------------------------------------------

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    //----------------------------------------------------------------------------------------------

    private void hideDialog() {
        if (pDialog.isShowing()&&!loadcomments&&!loadinfo&&!loadfav)
            pDialog.dismiss();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.house_view_menu, menu);
        mmenu = menu;
        return super.onCreateOptionsMenu(menu);

    }
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.hw_add_comment) {
            Bundle b=new Bundle();
            b.putString("marker_id",marker_id);
            b.putString("user_uid",User_uid);
            //Log.e("User_uid view ",User_uid);
            Intent i = new Intent(Houseview.this, HouseAddComment.class);
            i.putExtra("bundle", b);
            startActivityForResult(i,1);
            return true;
        }

        if (id == R.id.hw_favorite) {
            if (!favstate) {
                LoadHouseFavStateSet(marker_id);
                LoadHouseFavState(marker_id);
                return true;
            }
            if (favstate) {
                LoadHouseFavStateDelete(marker_id);
                LoadHouseFavState(marker_id);
                return true;
            }
            return true;
        }
        if (id == R.id.hw_action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //----------------------------------------------------------------------------------------------

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    //----------------------------------------------------------------------------------------------

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            loadinfo = false;
            hideDialog();
            JSONObject jObj;
            try {
                jObj = new JSONObject(result);
                boolean error = jObj.getBoolean("error");
               // Log.e("ccc ",jObj.toString());
                if (!error) {
                    //Log.d("ssss",jObj.toString());
                    tvhvId.setText("ID " + AppConfig.myFormat(jObj.getString("id")));
                    tvhvAddress.setText(AppConfig.myFormat(jObj.getString("address")));

                    if (jObj.getString("houseinfo").isEmpty()) {
                        tvhvHouseInfo.setVisibility(View.GONE);
                    }else{
                        tvhvHouseInfo.setText(AppConfig.myFormat(jObj.getString("houseinfo")));
                    }

                    if (jObj.getString("developer").isEmpty()) {
                        tvhvDeveloper.setVisibility(View.GONE);
                    }else{
                        tvhvDeveloper.setText(AppConfig.myFormat(jObj.getString("developer")));
                    }
                    tvhvUsername.setText(AppConfig.myFormat(jObj.getString("username")));


                    tvhvDate.setText(getResources().getString(R.string.date_dev_begin) + " "+
                            jObj.getString("develop_begin_at")+ " "+
                            getResources().getString(R.string.date_dev_finish) + " "+
                            jObj.getString("develop_finish_at"));

                    tvhvStage.setText(getResources().getString(R.string.stage_construct) + ": "+
                            stageDBToHuman(jObj.getString("stage")));

                    if (User_uid.equals(jObj.getString("user_uid"))){
                        bthvEdit.setVisibility(View.VISIBLE);
                    } else {
                        bthvEdit.setVisibility(View.GONE);
                    }
                }
                else
                {
                    String errorMsg = jObj.getString("error_msg");
                    Log.d("LoadHouseInfo errorMsg -------"+errorMsg, marker_id);
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //----------------------------------------------------------------------------------------------

    private class HttpAsyncTask_Comments extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            loadcomments = false;
            hideDialog();
            try {
                // catch json with comments from server
                JSONArray jObj = new JSONArray(result);
                comments.clear();
                LinearLayout linLayout = (LinearLayout) findViewById(R.id.hw_list);
                linLayout.removeAllViews();

                    // make comments for every record from db
                    for (int i = 0; i < jObj.length(); i++) {
                        JSONObject jsonObj = jObj.getJSONObject(i);
                       // tvhvDeveloper.setText(tvhvDeveloper.getText()+jsonObj.getString("filename"));

                        if (jsonObj.getString("filename").equals("none")) {
                            comments.add(inflater.inflate(R.layout.hw_list_item_comment_wo_photo, null, false));
                        }else{
                            comments.add(inflater.inflate(R.layout.hw_list_item, null, false));
                        }

                        linLayout.addView(comments.get(i));

                        View item = comments.get(i);

                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("click", view.toString() );
                                if (view instanceof ImageView) {

                                    ImageView imageView = (ImageView) view;
                                    // do what you want with imageView
                                    Log.e("filename=", imageView.getTag().toString() );
                                    Bundle b=new Bundle();
                                    b.putString("filename",imageView.getTag().toString());
                                    Intent i = new Intent(Houseview.this, FullScreenViewActivity.class);
                                    i.putExtra("bundle", b);
                                    startActivityForResult(i,1);

                                }
                            }

                        });
                        TextView comment = (TextView) item.findViewById(R.id.ls_comment);

                        String s = java.net.URLDecoder.decode(jsonObj.getString("comment"));
                        comment.setText(s);

                        TextView username = (TextView) item.findViewById(R.id.ls_username);
                        username.setText(jsonObj.getString("username"));

                        TextView date = (TextView) item.findViewById(R.id.ls_date);
                        date.setText(jsonObj.getString("date"));


                        if (jsonObj.getString("filename")!="none") {
                            final String url=AppConfig.site+"thumbs/" + jsonObj.getString("filename");
                            final String jsonstr = jsonObj.getString("filename");
                            final ImageView iv = (ImageView) item.findViewById(R.id.ls_image);

                            Thread t = new Thread()  {
                                @Override
                                public void run() {
                                    try {
                                        final Bitmap bitmap = BitmapFactory.decodeStream(new URL(url).openStream());

                                        iv.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                iv.setImageBitmap(bitmap);
                                                iv.setTag(jsonstr);

                                                iv.setOnClickListener(new ImageView.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    // do what you want with imageView
                                                    Log.e("filename=", v.getTag().toString() );
                                                    Bundle b=new Bundle();
                                                    b.putString("filename",v.getTag().toString());
                                                    Intent i = new Intent(Houseview.this, FullScreenViewActivity.class);
                                                    i.putExtra("bundle", b);
                                                    startActivityForResult(i,1);                                                     }
                                                });
                                            }
                                        });
                                    } catch (Exception e) { e.printStackTrace(); }
                                };
                            };


                            t.start();

                        }

                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

   //----------------------------------------------------------------------------------------------

    private class HttpAsyncTask_FavState extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            loadfav = false;
            hideDialog();
            JSONObject jObj;
            try {
                // catch json with comments from server
                jObj = new JSONObject(result);
                Log.d("set",jObj.toString());
                if (jObj.getString("state").equals("true")){
                    mmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.icr_action_important));
                    favstate = true;
                }else{
                    mmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.icr_action_not_important));
                    favstate = false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private class HttpAsyncTask_FavStateDelete extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            hideDialog();
            mmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.icr_action_not_important));
            LoadHouseFavState(marker_id);

        }
    }

   //----------------------------------------------------------------------------------------------

    private class HttpAsyncTask_FavStateSet extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            hideDialog();
            mmenu.getItem(0).setIcon(getResources().getDrawable(R.drawable.icr_action_important));
            LoadHouseFavState(marker_id);
        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseInfo(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "loadhouseinfo";
        if(isConnected()){
            loadinfo = true;
            new HttpAsyncTask().execute(AppConfig.URL_MARKERS+"?tag="+tag_string_req+"&id="+marker_id+"&uid="+User_uid);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    (getResources().getString(R.string.site_not_response)), Toast.LENGTH_LONG).show();
        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseComments(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "comments_get";
        if(isConnected()){
            loadcomments = true;
            new HttpAsyncTask_Comments().execute(AppConfig.URL_COMMENTS+"?tag="+tag_string_req+"&marker_id="+marker_id+"&uid="+User_uid);
        }
        else{

        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseFavState(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "fav_get";
        if(isConnected()){

            loadfav = true;
            new HttpAsyncTask_FavState().execute(AppConfig.URL_FAV+"?tag="+tag_string_req+"&marker_id="+marker_id+"&uid="+User_uid);
        }
        else{

        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseFavStateSet(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "fav_set";
        if(isConnected()){
            showDialog();
            new HttpAsyncTask_FavStateSet().execute(AppConfig.URL_FAV+"?tag="+tag_string_req+"&marker_id="+marker_id+"&uid="+User_uid);
        }
        else{

        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseFavStateDelete(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "fav_delete";
        if(isConnected()){
            showDialog();
            new HttpAsyncTask_FavStateDelete().execute(AppConfig.URL_FAV+"?tag="+tag_string_req+"&marker_id="+marker_id+"&uid="+User_uid);
        }
        else{

        }
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

    public static String stageDBToHuman(String str)
    {
        String str2 = ArrayHuman[0];
        for( int i=0; i< ArrayDB.length ; i ++ )
            if( ArrayDB[ i ].equals(str))
                str2 = ArrayHuman[i];
        return str2;
    }

}

