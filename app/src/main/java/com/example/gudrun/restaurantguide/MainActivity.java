package com.example.gudrun.restaurantguide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    Spinner spinner;
    Button OSMButton;

    double pLong = 0;
    double pLat = 0;

    // @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        spinner = findViewById(R.id.idspinner);
        String[] distancevalues = {"15000", "20000", "30000"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, distancevalues);
        spinner.setAdapter(adapter);
        int value = Integer.parseInt((String) spinner.getSelectedItem());



        getLocation();


        OSMButton = findViewById(R.id.button);
        OSMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOSM();

            }
        });


    }

    private GpsTracker gpsTracker;

    void callOSM() {

        final int radius = Integer.parseInt((String) spinner.getSelectedItem());

        Log.e("Longitude: ", String.valueOf(pLong));
        Log.e("Latitude: ", String.valueOf(pLat));
        Log.e("Radius: ", String.valueOf(radius));

        //  new NetworkAsyncTask().execute();
        final NetworkAsyncTask httpsTask = new NetworkAsyncTask(radius, pLong, pLat);
        httpsTask.execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object response = httpsTask.get();
                    Log.e("Response: ", response.toString());
                    //NodeList nodeList = parsexml();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//  //                              showResponse.setText(Objects.toString(response));
//
//                            }
//                        });
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    intent.putExtra("nodeList", Objects.toString(response));
                    intent.putExtra("ownlon", pLong);
                    intent.putExtra("ownlat", pLat);
                    intent.putExtra("radius", radius);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getLocation()
    {
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation())
        {
            pLat = gpsTracker.getLatitude();
            pLong = gpsTracker.getLongitude();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    /*
    <osm-script>
  <query type="node">
    <has-kv k="amenity" v="restaurant"/>
<has-kv k="wheelchair" v="yes"/>
<around radius="1000.0" lat="41.89248629819397" lon="12.51119613647461"/>
  </query>
  <print/>
</osm-script>
     */


}

//TODO - set the put request and store result in a file.

