package com.housedevelop.housedevelop;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import java.util.*;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.content.res.Resources;
import android.app.SearchManager;
import android.view.MenuInflater;
import android.widget.AutoCompleteTextView;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.IOException;

import android.text.TextWatcher;
import android.os.AsyncTask;
import java.io.BufferedReader;

import android.text.Editable;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import android.widget.SimpleAdapter;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.location.Geocoder;
import android.location.Address;


public class MapsActivity extends ActionBarActivity  {
    GoogleMap mMap; // Might be null if Google Play services APK is not available.
    String Username;
    String Email;
    public String User_uid;
    Integer MapType;
    SharedPreferences sp;
    CameraPosition CameraCurrent;
    CameraPosition CameraLastLoadMarker;
    CameraPosition CameraMemory;
    /** Called when the activity is first created. */
    SearchManager msearchManager;

    ArrayList<Marker> markers = new ArrayList<>();

    private SQLiteHandler db;
    private SessionManager session;

    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    LatLng pt=null;

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null && address.size() > 0) {
                float lat = (float) (address.get(0).getLatitude());
                float lng = (float) (address.get(0).getLongitude());
                Log.d("pos ",String.valueOf(lat)+" "+String.valueOf(lng));
                pt = new LatLng(lat, lng);
                return pt;
            }
        }
        catch(IOException e)
        {
            return null;
        }
        return pt;
    }
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(Color.RED);
            }
        }
        db = new SQLiteHandler(getApplicationContext());

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        db.getUserDetails();

//        Username = user.get("name");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Username = sp.getString("username","");
        Email = sp.getString("email","");
        User_uid = sp.getString("user_uid","");

        setUpMapIfNeeded();
        SetMapType();
        if (mMap!=null)
        {
            markers.clear();
            //LoadMarkers(mMap.getCameraPosition().target, LengthUnit.ZoomInMeters[(int)mMap.getCameraPosition().zoom]/1000);
        }
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString());

                Log.d("QUERY task", "New text is " + s.toString());
            }

                @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        atvPlaces.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                Log.d("description","test");
                HashMap<String, String> hm;
                hm = (HashMap<String, String>) parent.getItemAtPosition(pos);
                String str = hm.get("description");
                Log.d("description",str);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress(str)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                atvPlaces.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
            }
        });
        atvPlaces.setVisibility(View.GONE);
    }

    //----------------------------------------------------------------------------------------------

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();

       //f mMap.clear();
        setUpMapIfNeeded();
        SetMapType();

        if (mMap!=null)
        {
            markers.clear();
            if (mMap.getCameraPosition().zoom<18)
            {
                LoadMarkers(mMap.getCameraPosition().target, LengthUnit.ZoomInMeters[(int)mMap.getCameraPosition().zoom]/1000);

            }else{
                LoadMarkers(mMap.getCameraPosition().target, LengthUnit.ZoomInMeters[18]/1000);
            }
        }

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

       // MenuItem searchItem = menu.findItem(R.id.main_action_search);
        //mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //mSearchView.setQueryHint("Поиск");
        //mSearchView.setOnQueryTextListener(this);

        msearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //mSearchView.setSearchableInfo(
          //      msearchManager.getSearchableInfo(getComponentName()));
//        setupSearchView(searchItem);
        return super.onCreateOptionsMenu(menu);
    }


    //----------------------------------------------------------------------------------------------
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb;
            sb = new StringBuffer();

            String line ;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception loading url", e.toString());
        }finally{
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //----------------------------------------------------------------------------------------------
    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            String input="";
            Log.d("input",place[0]);
            //String str=String.valueOf(profile.get("text")).replace(s, w).replace(q, w);;
            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            //String types = "types=geocode";

            // Sensor enabled
           // String sensor = "sensor=false";

            // Building the parameters to the web service
            //String output = "json";

            // Building the url to the web service
            String url = "http://housedevelop.com/place_api.php?"+input+"&username="+User_uid;
            Log.d("Query send",url);
            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();
            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    //----------------------------------------------------------------------------------------------

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };
//            Log.d("list place ", result.toString());
            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlaces.setAdapter(adapter);
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main_action_fav) {
//            tvLocation.setText(Username);
            Bundle b=new Bundle();
            b.putString("User_uid",User_uid);

            Intent intent = new Intent(MapsActivity.this, FavViewActivity.class);
            intent.putExtra("bundle", b);
            startActivity(intent);


            return true;

          }
        if (id == R.id.main_action_settings) {
//            tvLocation.setText(Username);
              Intent intent = new Intent(MapsActivity.this, UserSettingActivity.class);
              startActivity(intent);
              return true;
          }
        if (id == R.id.main_action_search) {
              if (atvPlaces.isShown()) {
                  InputMethodManager imm = (InputMethodManager)getSystemService(
                          Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                  atvPlaces.setVisibility(View.GONE);

                  return true;
              }
            atvPlaces.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            atvPlaces.requestFocus();
            return true;

          }
        if (id == R.id.main_action_login) {
              logoutUser();
              return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);

        //if (resultCode == RESULT_OK){
            if (data == null) {return;}
            String OK = data.getStringExtra("OK");
            //Log.d("myLogs", "OK = " + OK);
            //Log.d("LoadMarkers --------1", "");
            //tvLocation.setText("LoadMarkers --------1" + OK);
            if ((OK.equals("OK"))||(OK.equals("cancel")))
            {
                markers.clear();
                CameraLastLoadMarker=null;
                Log.d("LoadMarkers --------2", "");
                LoadMarkers(mMap.getCameraPosition().target, LengthUnit.ZoomInMeters[(int)mMap.getCameraPosition().zoom]/1000);
            }

        //} else {
          //} else {
      //  Toast.makeText(this, (getResources().getString(R.string.err_wronganswer)), Toast.LENGTH_SHORT).show();
    //}
    }

    //----------------------------------------------------------------------------------------------

    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
        }
    }

    //----------------------------------------------------------------------------------------------

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {

                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
                    TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
                    tvTitle.setText(marker.getTitle());
                    TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
                    tvSnippet.setText(marker.getSnippet());
                    return myContentsView;
                }
            });
            SetMapType();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);

                // Getting Google Play availability status

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                //LoadMarkers(CameraCurrent.target, 20);

                //On long click to map add new marker
                mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng point) {
                        /*Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(point)
                                .title(getResources().getString(R.string.new_build))
                                .snippet("0")
                                        //.metadata = {type: "point", id: 1}
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/

                        Bundle b=new Bundle();
                        b.putString("username",Username);
                        b.putString("User_uid",User_uid);
                        b.putParcelable("latlng",point);

                        CameraMemory=CameraCurrent;
                        Intent i = new Intent(MapsActivity.this, activity_newbuild.class);
                        i.putExtra("bundle", b);
                        startActivityForResult(i,1);
                    }
                });

                        // On click to infowindow marker opens new activity with details
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        //tvLocation.setText(marker.getSnippet());

                        Bundle b=new Bundle();
                        b.putString("User_uid",User_uid);
                        b.putString("id",marker.getSnippet());

                        Intent i = new Intent(MapsActivity.this, Houseview.class);
                        i.putExtra("bundle", b);
                        startActivity(i);

                    }
                });

                // Current camera position
                mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                       // if (CameraLastLoadMarker!=null)
                        /*Log.e("Camera change ",String.valueOf(cameraPosition.target.longitude)+
                                ' '+String.valueOf(cameraPosition.target.latitude)+
                                ' '+String.valueOf(distance(cameraPosition.target,CameraLastLoadMarker.target,LengthUnit.KILOMETER))+
                                ' '+String.valueOf(LengthUnit.ZoomInMeters[(int)cameraPosition.zoom]/1000) );*/
                        if (cameraPosition.zoom<18) {
                        /*Toast.makeText(getApplicationContext(),
                                String.valueOf( ), Toast.LENGTH_LONG).show();
                        */
                            CameraCurrent = cameraPosition;
                            if (CameraCurrent.target != null)
                                LoadMarkers(CameraCurrent.target, LengthUnit.ZoomInMeters[(int) cameraPosition.zoom] / 1000);
                        }
                    }

                });
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
                // Showing status

                if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

                    Toast.makeText(getApplicationContext(),
                            status, Toast.LENGTH_LONG).show();

                } else { // Google Play Services are available

                    // Getting reference to the SupportMapFragment of activity_main.xml
                    SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                    // Getting GoogleMap object from the fragment
                    mMap = fm.getMap();

                    // Enabling MyLocation Layer of Google Map
                    mMap.setMyLocationEnabled(true);

                    // Getting LocationManager object from System Service LOCATION_SERVICE
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                    // Creating a criteria object to retrieve provider
                    Criteria criteria = new Criteria();

                    // Getting the name of the best provider
                    String provider = locationManager.getBestProvider(criteria, true);

                    // Getting Current Location
                    Location location = locationManager.getLastKnownLocation(provider);

                    if (location != null) {
                        onLocationChanged(location);
                    }
                    LocationListener locationListener = new MyLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, locationListener);

                    //tvLocation.setText( Integer.toString(location.getLatitude()));
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    private void LoadMarkers(final LatLng latLng, final Integer dist) {
            // Tag used to cancel the request

        //if (CameraLastLoadMarker==null) CameraLastLoadMarker = CameraCurrent;
        Log.d("LoadMarkers -----------", String.valueOf(latLng.longitude)+" "+String.valueOf(latLng.latitude)+ " "+String.valueOf(dist)+"\n");
        if ((CameraLastLoadMarker==null)||
                (distance(latLng,CameraLastLoadMarker.target)>LengthUnit.ZoomInMeters[(int)CameraLastLoadMarker.zoom]/1000)||
                (CameraLastLoadMarker.zoom!=CameraCurrent.zoom))
        {
            Log.d("LoadMarkers", "----------- markers_get\n");
            String tag_string_req = "markers_get";
            //RequestQueue queue = Volley.newRequestQueue(this);
            CameraLastLoadMarker = CameraCurrent;


            StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_MARKERS,
                    new Response.Listener<String>() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(String response) {
                            //Log.d("getmarker", "GetMarkers : " + response.toString());

                            try {
                                JSONArray jObj = new JSONArray(response);
                                /*JSONParser jsonParser = new JSONParser();
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());*/

                                // Check for error node in json

                                Integer Counter=0;

                                    for (int i = 0; i < jObj.length(); i++) {

                                        JSONObject jsonObj = jObj.getJSONObject(i);
                                        Boolean AlreadyExist = false;

                                        if (markers!=null)
                                        for (int k = 0; k < markers.size(); k++)
                                        {
                                            Marker currentMarker = markers.get(k);
                                            LatLng pos = new LatLng(
                                                    Float.valueOf(jsonObj.getString("lat")),
                                                    Float.valueOf(jsonObj.getString("lng")));

                                            LatLng posmark = new LatLng(currentMarker.getPosition().latitude,
                                                    currentMarker.getPosition().longitude);

                                            if (distance(pos,posmark)<0.01) AlreadyExist=true;

                                        }
                                        if (!AlreadyExist)
                                        {
                                            String titl;
                                            if (!jsonObj.getString("developer").isEmpty())
                                            {
                                                titl=AppConfig.myFormat(jsonObj.getString("address"))+
                                                        " ("+AppConfig.myFormat(jsonObj.getString("developer")).trim()+")";
                                            } else
                                            {
                                                titl=AppConfig.myFormat(jsonObj.getString("address"));
                                            }
                                            Marker marker = mMap.addMarker(new MarkerOptions()
                                                        .title(titl)
                                                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable. house_flag))
                                                        //.anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                                        .snippet(jsonObj.getString("id"))
                                                        .position(new LatLng(
                                                                Float.valueOf(jsonObj.getString("lat")),
                                                                Float.valueOf(jsonObj.getString("lng"))
                                                        ))
                                            );
                                            markers.add(marker);
                                            Counter++;
                                        }

                                }
                                Log.d("Counter = ", String.valueOf(Counter));
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("mark_error", "GetMarkers Error: " +
                            String.valueOf(latLng.latitude)+
                            String.valueOf(latLng.longitude)+
                            error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                protected java.util.Map<String, String> getParams() {
                    // Posting parameters to login url
                    java.util.Map<String, String> params = new HashMap<>();
                    params.put("tag", "markers_get");
                    params.put("lat", String.valueOf(latLng.latitude));
                    params.put("lon", String.valueOf(latLng.longitude));
                    params.put("dist",String.valueOf(dist));

                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

    //----------------------------------------------------------------------------------------------

    void SetMapType() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        MapType = Integer.parseInt(sp.getString("maptype","0"));
        //tvLocation.setText(String.valueOf(MapType));

      /*  switch (MapType) {
            case 0:mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            case 1:mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            case 2:mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            case 3:mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }*/

        if (MapType==0)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        if (MapType==1)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        if (MapType==2)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        if (MapType==3)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

    }

    //----------------------------------------------------------------------------------------------

    //@Override
    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

    }

    //----------------------------------------------------------------------------------------------

    public static double distance(LatLng point1, LatLng point2) {
        return distanceInRadians(point1, point2) * 6371;
    }

    //----------------------------------------------------------------------------------------------

    public static double distanceInRadians(LatLng point1, LatLng point2) {
        double lat1R = Math.toRadians(point1.latitude);
        double lat2R = Math.toRadians(point2.latitude);
        double dLatR = Math.abs(lat2R - lat1R);
        double dLngR = Math.abs(Math.toRadians(point2.longitude - point1.longitude));
        double a = Math.sin(dLatR / 2) * Math.sin(dLatR / 2) + Math.cos(lat1R) * Math.cos(lat2R)
                * Math.sin(dLngR / 2) * Math.sin(dLngR / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    //----------------------------------------------------------------------------------------------

/*    public int getZoomLevel(Circle circle) {
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            return  (int) (16 - Math.log(scale) / Math.log(2));
        }
        else return 0;
    }*/
}
