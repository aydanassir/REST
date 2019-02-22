package com.example.aydan.resturantguidefinal;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Compass extends AppCompatActivity implements SensorEventListener,LocationListener {
    private GpsTracker gpsTracker;
    double pLat;
    double pLong;
    double hotellat;
    double hotelon;
    ImageView arrows;
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    SensorManager mSensorManager;
    Sensor sensor;
    ImageView compass;
    TextView distances;
    TextView bearin;
    TextView headings;
    String names;
    TextView compassname;

    Location userLoc=new Location("service Provider");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        getMyLocation();

        getSupportActionBar().setTitle("Restaurant Guide");
        getSupportActionBar().setSubtitle("Direction of Hotel" +names);
        arrows=findViewById(R.id.arrow);
        compass=findViewById(R.id.imageCompass);
        distances=findViewById(R.id.dist);
        bearin=findViewById(R.id.bearing);
        headings=findViewById(R.id.heading);
        compassname=findViewById(R.id.namecompass);

        hotellat = getIntent().getDoubleExtra("lat", 0.0);
        hotelon = getIntent().getDoubleExtra("lon", 0.0);

        names=getIntent().getStringExtra("name");
        getSupportActionBar().setTitle("Restaurant Guide");
        getSupportActionBar().setSubtitle(names);

        mSensorManager =  (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);



        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new Compass();

        //The minimum distance will be the distance selected
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3000, ll);




    }


    public void getMyLocation()
    {
        gpsTracker = new GpsTracker(Compass.this);
        if(gpsTracker.canGetLocation())
        {
            pLat = gpsTracker.getLatitude();
            pLong = gpsTracker.getLongitude();

            userLoc.setLongitude(pLong);
            userLoc.setLatitude(pLat);


        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        double pk = (180.d/Math.PI);
        double a1 = pLat / pk;
        double a2 = pLong / pk;
        double b1 = hotellat / pk;
        double b2 = hotelon / pk;
        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        double distance1= 6450000 * tt;
        double distancekm=Math.round(distance1)/1000.0;
        double distancem=Math.round(distance1*100.0)/100.0;

        //distance=distance/1000;
       // distmeters.setText("Distance in Meter:-"+String.valueOf(distance)+"M");
        distances.setText("Distance:-"+String.valueOf(distancem)+"M"+","+
                "("+String.valueOf(distancekm)+"KM"+")");


        if(distancekm<0.01){

        }
        float degree = Math.round(sensorEvent.values[0]);
        float head = Math.round(sensorEvent.values[0]);
        float bearTo;
        Location destinationLoc = new Location("service Provider");
        destinationLoc.setLatitude(hotellat); //hotel latitude setting
        destinationLoc.setLongitude(hotelon); //hotel longitude setting
        bearTo=userLoc.bearingTo(destinationLoc);//calculate bear


        bearin.setText("Bearing:"+String.valueOf(bearTo));
        headings.setText("Heading: " + String.valueOf(degree) + " degrees");
            //bearTo = The angle from true north to the destination location from the point we're your currently standing.
            //head = The angle that you've rotated your phone from true north.


        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head -= geoField.getDeclination(); // converts magnetic north into true north

        if (bearTo < 0) {
            bearTo = bearTo + 360;
            //bearTo = -100 + 360  = 260;
        }
//This is where we choose to point it
        float direction = bearTo - head;

// If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        Animation an = new RotateAnimation(currentDegreeNeedle,  direction,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentDegreeNeedle = direction;

        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        arrows.startAnimation(an);

        RotateAnimation ra=new RotateAnimation(currentDegree,-degree,Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(120);
        ra.setFillAfter(true);
        compass.startAnimation(ra);
        currentDegree=-degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        getMyLocation();
    }

   @Override
    public void onLocationChanged(Location location) {


      /* double lat;
       double lon;
       lat=location.getLatitude();
       lon=location.getLongitude();
       double pk = (180.d/Math.PI);
        double a1 = lat / pk;
        double a2 = lon / pk;
        pk = (180.d/Math.PI);

       double b1 = hotellat / pk;
       double b2 = hotelon / pk;
       double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
       double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
       double t3 = Math.sin(a1) * Math.sin(b1);
       double tt = Math.acos(t1 + t2 + t3);
       double distance1= 6450000 * tt;
       double distancekm=Math.round(distance1)/1000.0;
       double distancem=Math.round(distance1*100.0)/100.0;

       //distance=distance/1000;
       // distmeters.setText("Distance in Meter:-"+String.valueOf(distance)+"M");
       distances.setText("Distance:-"+String.valueOf(distancem)+"M"+","+
               "("+String.valueOf(distancekm)+"KM"+")");*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

