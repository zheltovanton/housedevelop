package com.housedevelop.housedevelop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import android.location.Geocoder;
import android.content.Context;
import android.location.Address;
import android.location.Location;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import android.os.Handler;
import android.os.Message;
import android.app.ProgressDialog;
import org.json.JSONObject;
import java.io.InputStream;
import org.json.JSONException;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import android.text.InputType;
import android.app.DatePickerDialog.OnDateSetListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;

public class activity_newbuild extends Activity {
    public TextView tvAddress;
    TextView tvUsername;
    TextView tvLatLng;
    TextView tvcity;
    TextView tvcountry;
    TextView tvdeveloper;
    TextView tvdeveloper_details;
    TextView tvhousedetails;
    TextView tvregion;
    TextView tvdevelop_begin_at;
    TextView tvdevelop_finish_at;
    TextView tvstage;
    Spinner spinner_stage;
    Button btSave;
    Button btCancel;
    String User_uid;
    String tvstage2 = "permission";

    LatLng latlng;
    private ProgressDialog pDialog;
    //String location_string;

    private int year;
    private int month;
    private int day;

    private DatePickerDialog tvdevelop_begin_atDialog;
    private DatePickerDialog tvdevelop_finish_atDialog;
    private SimpleDateFormat dateFormatter;
    public static String[] ArrayHuman;
    public static String[] ArrayDB;


    private static final String URL = "http://maps.googleapis.com/maps/api/geocode/xml";

     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

         setContentView(R.layout.activity_newbuild);

         ArrayDB = getResources().getStringArray(R.array.addhouse_stage_build_values);
         ArrayHuman = getResources().getStringArray(R.array.addhouse_stage_build_titles);

         dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

         tvAddress = (TextView) findViewById(R.id.nb_txt_address);
         tvUsername = (TextView) findViewById(R.id.nb_creator_username);
         tvLatLng = (TextView) findViewById(R.id.nb_txt_latlng);
         tvcity = (TextView) findViewById(R.id.nb_txt_city);
         tvcountry = (TextView) findViewById(R.id.nb_txt_country);
         tvdeveloper = (TextView) findViewById(R.id.nb_txt_developer);
         tvdeveloper_details = (TextView) findViewById(R.id.nb_txt_developer_details);
         tvhousedetails = (TextView) findViewById(R.id.nb_txt_housedetails);
         tvregion = (TextView) findViewById(R.id.nb_txt_region);
         tvdevelop_begin_at = (TextView) findViewById(R.id.nb_txt_develop_begin_at);
         tvdevelop_finish_at = (TextView) findViewById(R.id.nb_txt_develop_finish_at);
         spinner_stage = (Spinner) findViewById(R.id.nb_spinner_stage);

         ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                 R.array.addhouse_stage_build_titles, android.R.layout.simple_spinner_item);
         // Specify the layout to use when the list of choices appears
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
         spinner_stage.setAdapter(adapter);

         btSave = (Button) findViewById(R.id.nb_btn_save);
         btCancel = (Button) findViewById(R.id.nb_btn_cancel);


         // Progress dialog
         pDialog = new ProgressDialog(this);
         pDialog.setCancelable(true);

         Bundle bundle = getIntent().getParcelableExtra("bundle");
         tvUsername.setText(bundle.getString("username"));
         User_uid = bundle.getString("User_uid");

         tvLatLng.setText((getResources().getString(R.string.txt_coord)) + " " + bundle.getParcelable("latlng").toString());
         latlng = bundle.getParcelable("latlng");

         final Calendar c = Calendar.getInstance();
         year = c.get(Calendar.YEAR);
         month = c.get(Calendar.MONTH);
         day = c.get(Calendar.DAY_OF_MONTH);

         // set current date into textview
         tvdevelop_begin_at.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("-").append(month+1).append("-")
                .append(year).append(" "));

         // set current date into datepicker
         tvdevelop_finish_at.setText(new StringBuilder()
                // Month is 0 based, just add 1
                 .append(day).append("-").append(month+1).append("-")
                 .append(year).append(" "));
         tvdevelop_begin_at.setInputType(InputType.TYPE_NULL);
         tvdevelop_finish_at.setInputType(InputType.TYPE_NULL);

         setDateTimeField();

         showDialog();
         LocationAddress locationAddress = new LocationAddress();
         locationAddress.getAddressFromLocation(latlng.latitude, latlng.longitude,
                 getApplicationContext(), new GeocoderHandler());

         // Make reaction to button "Save"
         btSave.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 try {
                     saveMarker(tvAddress.getText().toString(),
                             tvUsername.getText().toString(),
                             User_uid,
                             String.valueOf(latlng.latitude),
                             String.valueOf(latlng.longitude),
                             tvcity.getText().toString(),
                             tvcountry.getText().toString(),
                             tvregion.getText().toString(),
                             tvdeveloper.getText().toString(),
                             tvdeveloper_details.getText().toString(),
                             tvhousedetails.getText().toString(),
                             tvdevelop_begin_at.getText().toString(),
                             tvdevelop_finish_at.getText().toString(),
                             spinner_stage.getSelectedItem().toString()
                     );
                 } catch (ParseException e) {
                     e.printStackTrace();
                 }
             }
         }        );

         // Make reaction to button "Cancel"
         btCancel.setOnClickListener(new View.OnClickListener() {

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
        tvdevelop_finish_at.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                tvdevelop_finish_atDialog.show();
            }
        });

        tvdevelop_begin_at.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                tvdevelop_begin_atDialog.show();
            }
        });

        Calendar newCalendar = Calendar.getInstance();
        tvdevelop_finish_atDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvdevelop_finish_at.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        tvdevelop_begin_atDialog= new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                tvdevelop_begin_at.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //----------------------------------------------------------------------------------------------

    private void saveMarker(final String txt_address,
                            final String creator_username,
                            final String creator_uid,
                            final String lat,
                            final String lng,
                            final String city,
                            final String country,
                            final String region,
                            final String developer,
                            final String developer_details,
                            final String housedetails,
                            final String develop_begin_at,
                            final String develop_finish_at,
                            final String stage
                            ) throws ParseException {
        // Tag used to cancel the request
        final String tag_string = "marker_add";

        RequestQueue queue = Volley.newRequestQueue(this);

        pDialog.setMessage(getResources().getString(R.string.txt_pr_saving));
        showDialog();

        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date inputdevelop_begin_at = fmt.parse(develop_begin_at+" 01:00");
        Date inputdevelop_finish_at = fmt.parse(develop_finish_at+" 01:00");

        // Create the MySQL datetime string
        fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String datedevelop_begin_at = fmt.format(inputdevelop_begin_at);
        final String datedevelop_finish_at = fmt.format(inputdevelop_finish_at);

        final String stage2=stageHumanToDB(stage);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_MARKERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("", "new marker: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // Marker successfully stored in MySQL
                        // Launch login activity
 /*                       Intent intent = new Intent(
                                activity_newbuild.this,
                                LoginActivity.class);
                        startActivity(intent);*/
                        Intent intent = new Intent();
                        intent.putExtra("OK", "OK");
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "Add error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to register url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("tag", tag_string);

                params.put("address",txt_address);
                params.put("creator_username",creator_username);
                params.put("creator_uid",creator_uid);
                params.put("lat",lat);
                params.put("lng",lng);
                params.put("city",city);
                params.put("country",country);
                params.put("region",region);
                params.put("developer",developer);
                params.put("developer_details",developer_details);
                params.put("housedetails",housedetails);
                params.put("develop_begin_at",datedevelop_begin_at.toString());
                params.put("develop_finish_at",datedevelop_finish_at.toString());
                params.put("stage",stage2);

                return params;
            }

        };

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        queue.add(strReq);
    }

    //----------------------------------------------------------------------------------------------

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    tvAddress.setText(locationAddress);
                    tvcity.setText(bundle.getString("city"));
                    tvcountry.setText(bundle.getString("country"));
                    tvregion.setText(bundle.getString("region"));
                    break;
                default:
                    locationAddress = null;
            }
//            Log.d("locationAddress", " <>"+locationAddress+"<>");

            hideDialog();

        }
    }

    //----------------------------------------------------------------------------------------------

    public static Address getAddress(final Context context,
                                     final Location location) {
        if (location == null)
            return null;

        final Geocoder geocoder = new Geocoder(context);
        final List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty()){

            return addresses.get(0);
        }

        else
            return null;
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
        for( int i=0; i< ArrayHuman.length ; i ++ )
            if( ArrayHuman[ i ].equals(str))
                return i;
        return 0;
    }
}


