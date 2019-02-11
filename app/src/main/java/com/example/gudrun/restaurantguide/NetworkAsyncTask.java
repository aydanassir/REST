package com.example.gudrun.restaurantguide;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class NetworkAsyncTask extends AsyncTask  {

    private final int radius;
    private final double strlongitude;
    private final double strlatitude;


    NetworkAsyncTask(int radius, double longitude, double latitude) {
        this.radius = radius;
        this.strlongitude = longitude;
        this.strlatitude = latitude;
    }

    String response = "";

    @Override
    protected Object doInBackground(Object[] objects) {
        URL url = null;
/*
        String call = "<osm-script>\n" + "  <query type=\"node\">\n" + "<has-kv k=\"amenity\" v=\"restaurant\"/>\n" +  "<has-kv k=\"wheelchair\" v=\"yes\"/>\n"+
        "<around radius=\"" + radius + "\" lat=\"" + latitude + "\" lon=\"" + longitude + "\"/>\n" +
                "  </query>\n" +
                "  <print/>\n" +
                "</osm-script>";
*/
        String call = " <osm-script>\n" +
                "  <query type=\"node\">\n" +
                "    <has-kv k=\"amenity\" v=\"restaurant\"/>\n" +
                "<has-kv k=\"wheelchair\" v=\"yes\"/>\n" +
                "<around radius=\""+radius+"\" lat=\""+strlatitude+"\" lon=\""+strlongitude+"\"/>\n" +
                "  </query>\n" +
                "  <print/>\n" +
                "</osm-script>";

        Log.e("Call:", call);
        try {
            url = new URL("https://lz4.overpass-api.de/api/interpreter");
            HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
            httpsCon.setDoOutput(true);
            httpsCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpsCon.getOutputStream());
            out.write(call);
            out.close();

            int responseCode=httpsCon.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(httpsCon.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            } else {
                response="";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("Reponse: ", response);
        return response;
    }

}

