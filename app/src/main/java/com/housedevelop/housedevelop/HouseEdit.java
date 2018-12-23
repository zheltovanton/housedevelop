package com.housedevelop.housedevelop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.drive.internal.s;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.net.URL;
import java.util.Locale;
import java.text.DateFormat;

import static java.util.Locale.UK;
import com.housedevelop.housedevelop.AppConfig;

public class HouseEdit extends ActionBarActivity {
    String User_uid;
    String marker_id;

    TextView ehUsername;
    EditText ehAddress;
    EditText ehcity;
    EditText ehcountry;
    EditText ehdeveloper;
    EditText ehdeveloper_details;
    EditText ehhousedetails;
    EditText ehregion;
    EditText ehdevelop_begin_at;
    EditText ehdevelop_finish_at;
    Spinner ehspinner_stage;
    Button ehbtSave;
    Button ehbtCancel;
    private DatePickerDialog ehdevelop_begin_atDialog;
    private DatePickerDialog ehdevelop_finish_atDialog;
    private SimpleDateFormat dateFormatter;

    LayoutInflater inflater;
    private ProgressDialog pDialog;
    Boolean loadinfo = false;
    public static String[] ArrayHuman;
    public static String[] ArrayDB;
    //----------------------------------------------------------------------------------------------

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edithouse);

        ArrayDB = getResources().getStringArray(R.array.addhouse_stage_build_values);
        ArrayHuman = getResources().getStringArray(R.array.addhouse_stage_build_titles);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        User_uid = bundle.getString("User_uid");
        marker_id = bundle.getString("id");
        inflater = getLayoutInflater();

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        ehAddress = (EditText) findViewById(R.id.eh_txt_address);
        ehUsername = (TextView) findViewById(R.id.eh_creator_username);
        ehcity = (EditText) findViewById(R.id.eh_txt_city);
        ehcountry = (EditText) findViewById(R.id.eh_txt_country);
        ehdeveloper = (EditText) findViewById(R.id.eh_txt_developer);
        ehdeveloper_details = (EditText) findViewById(R.id.eh_txt_developer_details);
        ehhousedetails = (EditText) findViewById(R.id.eh_txt_housedetails);
        ehregion = (EditText) findViewById(R.id.eh_txt_region);
        ehdevelop_begin_at = (EditText) findViewById(R.id.eh_txt_develop_begin_at);
        ehdevelop_finish_at = (EditText) findViewById(R.id.eh_txt_develop_finish_at);
        ehspinner_stage = (Spinner) findViewById(R.id.eh_spinner_stage);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.addhouse_stage_build_titles, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ehspinner_stage.setAdapter(adapter);

        ehbtSave = (Button) findViewById(R.id.eh_btn_save);
        ehbtCancel = (Button) findViewById(R.id.eh_btn_cancel);

        ehdevelop_begin_at.setInputType(InputType.TYPE_NULL);
        ehdevelop_finish_at.setInputType(InputType.TYPE_NULL);
        setDateTimeField();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.loading));
        showDialog();

        LoadHouseInfo(marker_id);
        // Make reaction to button "Save"
        ehbtSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    SaveHouseInfo(marker_id,
                            User_uid,
                            ehAddress.getText().toString(),
                            ehcity.getText().toString(),
                            ehregion.getText().toString(),
                            ehcountry.getText().toString(),
                            ehdeveloper.getText().toString(),
                            ehdeveloper_details.getText().toString(),
                            ehhousedetails.getText().toString(),
                            ehdevelop_begin_at.getText().toString(),
                            ehdevelop_finish_at.getText().toString(),
                            ehspinner_stage.getSelectedItem().toString()
                    );
                } catch (ParseException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        // Make reaction to button "Cancel"
        ehbtCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
/*                 Intent intent = new Intent(activity_newbuild.this,  MapsActivity.class);
                 startActivity(intent);*/
                Intent intent = new Intent();
                intent.putExtra("OK", "cancel");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

   //----------------------------------------------------------------------------------------------

    private void setDateTimeField() {
        ehdevelop_finish_at.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                ehdevelop_finish_atDialog.show();
            }
        });

        ehdevelop_begin_at.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                ehdevelop_begin_atDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        ehdevelop_finish_atDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                ehdevelop_finish_at.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        ehdevelop_begin_atDialog= new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                ehdevelop_begin_at.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //----------------------------------------------------------------------------------------------

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

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

    private class HttpAsyncTask_LoadInfo extends AsyncTask<String, Void, String> {
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

                if (!error) {
                    Log.d("ssss",jObj.toString());
                    ehAddress.setText(AppConfig.myFormat(jObj.getString("address2")));
                    ehcity.setText(AppConfig.myFormat(jObj.getString("city")));
                    ehregion.setText(AppConfig.myFormat(jObj.getString("region")));
                    ehcountry.setText(AppConfig.myFormat(jObj.getString("country")));
                    ehdeveloper.setText(AppConfig.myFormat(jObj.getString("developer2")));
                    ehdeveloper_details.setText(AppConfig.myFormat(jObj.getString("developer_details")));
                    ehhousedetails.setText(AppConfig.myFormat(jObj.getString("houseinfo")));
                    ehUsername.setText(AppConfig.myFormat(jObj.getString("username")));
                    try {
                        SimpleDateFormat readFormat = new SimpleDateFormat( "yyyy-MM-dd", java.util.Locale.getDefault());
                        SimpleDateFormat writeFormat = new SimpleDateFormat( "dd-MM-yyyy", java.util.Locale.getDefault());

                        java.util.Date convertedDate1 = readFormat.parse( jObj.getString("develop_begin_at") );
                        String formattedDate1 = writeFormat.format( convertedDate1 );
                        ehdevelop_begin_at.setText(formattedDate1);

                        java.util.Date convertedDate2 = readFormat.parse( jObj.getString("develop_finish_at") );
                        String formattedDate2 = writeFormat.format( convertedDate2 );
                        ehdevelop_finish_at.setText(formattedDate2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("stage", stageDB_ID(jObj.getString("stage")).toString()+" "+jObj.getString("stage"));
                    ehspinner_stage.setSelection(stageDB_ID(jObj.getString("stage")));


                }
                else
                {
                    String errorMsg = jObj.getString("error_msg");
                    Log.d("LoadHouseInfo errorMsg -------" + errorMsg, marker_id);
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException  e) {
                e.printStackTrace();
            }
        }
    }

    // --------------------------------------------------------------------------------------------

    private class HttpAsyncTask_SaveInfo extends AsyncTask<String, Void, String> {
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
            Log.d("json response str",result);
            try {
                jObj = new JSONObject(result);
                Log.d("json response",jObj.toString());
                boolean error = jObj.getBoolean("error");

                if (!error) {
                    Intent intent = new Intent();
                    intent.putExtra("OK", "OK");
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    String errorMsg = jObj.getString("error_msg");
                    Log.d("LoadHouseInfo errorMsg -------" + errorMsg, marker_id);
                    Toast.makeText(getApplicationContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // --------------------------------------------------------------------------------------------

    public void LoadHouseInfo(final String marker_id){
        // check if you are connected or not
        String tag_string_req = "loadhouseinfo";
        if(isConnected()){
            loadinfo = true;
            new HttpAsyncTask_LoadInfo().execute(AppConfig.URL_MARKERS+"?tag="+tag_string_req+"&id="+marker_id+"&uid="+User_uid);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    (getResources().getString(R.string.site_not_response)), Toast.LENGTH_LONG).show();
        }
    }

    // --------------------------------------------------------------------------------------------

    public void SaveHouseInfo(final String marker_id,
                              final String user_uid,
                              final String address,
                              final String city,
                              final String region,
                              final String country,
                              final String developer,
                              final String developer_details,
                              final String housedetails,
                              String develop_begin_at,
                              String develop_finish_at,
                              final String stage) throws ParseException, UnsupportedEncodingException {
        // check if you are connected or not
        String tag_string_req = "marker_edit";
        pDialog.setMessage(getResources().getString(R.string.txt_pr_saving));
        showDialog();

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

        Date inputdevelop_begin_at = null;
        try {
            inputdevelop_begin_at = fmt.parse(develop_begin_at);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        Date inputdevelop_finish_at = null;
        try {
            inputdevelop_finish_at = fmt.parse(develop_finish_at);
        }catch (ParseException e) {
            e.printStackTrace();
        }


        // Create the MySQL datetime string
        fmt = new SimpleDateFormat("yyyy-MM-dd");
        final String datedevelop_begin_at = fmt.format(inputdevelop_begin_at);
        final String datedevelop_finish_at = fmt.format(inputdevelop_finish_at);
        Log.d("stage ", stage);
        final String stage2=stageHumanToDB(stage);
        Log.d("stage ", stage);

        if(isConnected()){

            String url = AppConfig.URL_MARKERS+"?tag="+tag_string_req+
                    "&id="+marker_id+
                    "&uid="+user_uid+
                    "&address="+ URLEncoder.encode(address, "UTF-8")+
                    "&city="+URLEncoder.encode(city, "UTF-8")+
                    "&country="+URLEncoder.encode(country, "UTF-8")+
                    "&region="+URLEncoder.encode(region, "UTF-8")+
                    "&developer="+URLEncoder.encode(developer, "UTF-8")+
                    "&developer_details="+URLEncoder.encode(developer_details, "UTF-8")+
                    "&housedetails="+URLEncoder.encode(housedetails, "UTF-8")+
                    "&develop_begin_at="+URLEncoder.encode(datedevelop_begin_at, "UTF-8")+
                    "&develop_finish_at="+URLEncoder.encode(datedevelop_finish_at, "UTF-8")+
                    "&stage="+URLEncoder.encode(stage2, "UTF-8");
            Log.d("send save url: ",url);
            new HttpAsyncTask_SaveInfo().execute(url);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    (getResources().getString(R.string.site_not_response)), Toast.LENGTH_LONG).show();
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

    public static String stageHumanToDB(String str)
    {
        String str2 = ArrayDB[0];
        for( int i=0; i< ArrayHuman.length ; i ++ )
            if( ArrayHuman[ i ].equals(str))
                str2 = ArrayDB[i];
        return str2;
    }


    //----------------------------------------------------------------------------------------------

    public static Integer stageDB_ID(String str)
    {
        for( int i=0; i< ArrayDB.length ; i ++ )
            if( ArrayDB[ i ].equals(str))
                return i;
        return 0;
    }
}
