package com.example.gudrun.restaurantguide;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor compass;
    private ImageView image;
    private TextView compassAngle;
    private float currentDegree = 0f;
    double ownlat;
    double ownlon;
    double latd;
    double lond;


    TextView text;
    TextView bearing;
    int radius;
    double d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        Intent i = getIntent();
        String lat = getIntent().getStringExtra("lat");
        String lon = getIntent().getStringExtra("lon");

        ownlat = getIntent().getDoubleExtra("ownlat", 0.0);
        ownlon = getIntent().getDoubleExtra("ownlon", 0.0);
        radius=getIntent().getIntExtra("radius",1500);

        latd = getIntent().getDoubleExtra("lat", 0.0);
        lond = getIntent().getDoubleExtra("lon", 0.0);


        text = findViewById(R.id.distance);
        //bearing
        bearing = findViewById(R.id.bearing);

        calculatedistance(ownlat, ownlon, radius, latd,lond);
        calculatebearing(ownlat, ownlon, latd,lond);

        System.out.println("lat: " + lat + "    lon: " + lon);
        System.out.println("Hui");
        getSupportActionBar().setTitle("Restaurant Guide");
        getSupportActionBar().setSubtitle("Compass");
        //Compass
        image = (ImageView) findViewById(R.id.imageViewCompass);
        compassAngle = (TextView) findViewById(R.id.angle);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (compass != null) {
            sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        compassAngle.setText("Heading : " + Float.toString(degree) + " degrees");
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // method to Compute distance according to haversine formula
    public Double calculatedistance(double latitude1, double longitude1, int radius, double latitude2, double longitude2) {
    int R = radius;//chosen radius in meter

        double latoneRad = Math.toRadians(latitude1);//convert latitude one degree to radian
        double lattwoRad = Math.toRadians(latitude2);//convert latitude two degree to radian

        double latdiffRad = Math.toRadians(latitude2 - latitude1);
        double londiffRad = Math.toRadians(longitude2 - longitude1);
        //calculate a
        double a = Math.sin(latdiffRad / 2) * Math.sin(latdiffRad / 2) + Math.cos(latoneRad) * Math.cos(lattwoRad) * Math.sin(londiffRad / 2) * Math.sin(londiffRad / 2);
        //calculate c
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //calculate d
        double d = R * c;
        double roundoffdistance = Math.round(d * 100.0) / 100.0;
        text.setText("Distance: "+String.valueOf(roundoffdistance));
       // toRadians(d);
         return roundoffdistance;




    }
   // public Double toRadians(double d) {

          // double distance=    d * (Math.PI/180);
          // dist.setText("distancein km: "+String.valueOf(distance));
        //return distance;
    //}
    // method to Compute bearing
    public Double calculatebearing(double latitude1, double longitude1,double latitude2,double longitude2) {
        double lat1Rad = Math.toRadians(latitude1);//convert latitude one degree to radian
        double lat2Rad = Math.toRadians(latitude2);//convert latitude two degree to radian
        double londiff2Rad = Math.toRadians(longitude2 - longitude1);//difference of longitude 1 & 2 to radian
        //calculate y
        double y = (Math.sin(londiff2Rad) * Math.cos(lat2Rad));
        //calculate x
        double x = (Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(londiff2Rad));
        //calculate bearing formula
        double b = Math.toDegrees(Math.atan2(y, x));

        double bearingroundOff = Math.round(b * 100.0) / 100.0;
        bearing.setText("Bearing:"+String.valueOf(bearingroundOff));

        return bearingroundOff;
    }


}
