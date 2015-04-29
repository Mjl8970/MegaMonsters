package com.example.root.reportlocation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainGameActivity extends ActionBarActivity implements OnMapReadyCallback {

    Button btnAttack;
    Button btnLogoff;
    Button btnRefresh;
    GPSTracker gps;
    JSONArray locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        btnAttack = (Button) findViewById(R.id.attackButton);
        btnLogoff = (Button) findViewById(R.id.logoffButton);
        btnRefresh = (Button) findViewById(R.id.refreshButton);

        stuff();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mainMap);
        mapFragment.getMapAsync(this);

        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainMenuActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnLogoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/logout.php?id=" + Player.getId());
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View vg = findViewById(R.id.MainGameActivity);
                vg.invalidate();
                Intent intent = new Intent(vg.getContext(), MainGameActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    public void stuff(){
        String url = "http://ec2-52-1-68-245.compute-1.amazonaws.com/requestLocations.php";
        try {
            try {
                new RequestTask2().execute(url).get(5000, TimeUnit.MILLISECONDS);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onMapReady(GoogleMap map) {

        gps = new GPSTracker(MainGameActivity.this);
        if(gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Player.longitude = longitude;
            Player.latitude = latitude;
            System.out.println("Longitude: " + longitude);
            System.out.println("Latitude: " + latitude);
            try {
                new RequestTask().execute("http://ec2-52-1-68-245.compute-1.amazonaws.com/update_location.php?id="+Player.getName()+"&lon="+Double.toString(longitude)+"&lat="+Double.toString(latitude)).get(6000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } else {
            gps.showSettingsAlert();
        }

        LatLng self = new LatLng(Player.getLatitude(),Player.getLongitude());

        map.setMyLocationEnabled(true);
        //map.getUiSettings().setZoomControlsEnabled(false);
        //map.getUiSettings().setAllGesturesEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 18));

        map.addMarker(new MarkerOptions()
                .title("Self")
                .snippet("It's you dummy")
                .position(self)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        if (!(locations == null)) {
            for (int i = 0; i < locations.length(); i++) {
                JSONObject json_data;
                try {
                    json_data = locations.getJSONObject(i);
                    String temp = json_data.toString();
                    System.out.println(temp);
                    String[] coordinates = temp.split("\"");
                    for(int j = 0; j < coordinates.length; j++){
                        System.out.println(j + " : " + coordinates[j]);
                    }
                    LatLng player = new LatLng(Double.parseDouble(coordinates[7]), Double.parseDouble(coordinates[3]));
                    System.out.println(Double.parseDouble(coordinates[3]));
                    System.out.println(Double.parseDouble(coordinates[7]));
                    map.addMarker(new MarkerOptions()
                            .title("Enemy")
                            .snippet("Fight him!")
                            .position(player)
                            );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class RequestTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String result = "";
//the year data to send
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("year","1980"));

//http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://ec2-52-1-68-245.compute-1.amazonaws.com/requestLocations.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    is.close();

                    result = sb.toString();
                } catch (IOException e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }


            }catch(Exception e){
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
//convert response to string

//parse json data
            try{
                locations = new JSONArray(result);

            }
            catch(JSONException e){
                e.printStackTrace();
            }
            // just in case
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }

    }

}


