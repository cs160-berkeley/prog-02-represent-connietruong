package com.github.connietruong.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Connie on 3/10/2016.
 */
public class RandomizerService extends Service {
    private String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
    private String geocodeApiKey = "AIzaSyAnvSjJX5-QrpqDFx-smcrUdKw4sUCHjC4";
    final String sunlightApiKey = "100a77ba6f7e460b9ff1ac18a3e24113";
    private final String sunlightUrl = "http://congress.api.sunlightfoundation.com/legislators/locate";
    private JSONArray counties;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        try {
            InputStream stream = getAssets().open("election-county-2012.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            counties = new JSONArray(new String(buffer, "UTF-8"));
            runRandomizer();
        } catch (IOException e) {

        } catch (JSONException e) {

        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void runRandomizer() {
        try {
            Integer random = 0 + (int) (Math.random() * ((counties.length() - 0) + 1));
            JSONObject randomCounty = counties.getJSONObject(random);
            final String countyName = randomCounty.getString("county-name");
            final String stateName = randomCounty.getString("state-postal");
            String findLatHttpCall = geocodeUrl + "address=" + countyName + ",+" + stateName + "&key=" + geocodeApiKey;
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, findLatHttpCall, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject latLong = response.getJSONArray("results").getJSONObject(0)
                                        .getJSONObject("geometry")
                                        .getJSONObject("location");
                                final String latitude = Double.toString(latLong.getDouble("lat"));
                                final String longitude = Double.toString(latLong.getDouble("lng"));
                                String locationHttpCall = sunlightUrl + "?latitude=" + latitude +
                                        "&longitude=" + longitude + "&apikey=" + sunlightApiKey;
                                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                        (Request.Method.GET, locationHttpCall, null, new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    Integer rep_count = response.getInt("count");
                                                    if (rep_count == 0) {
                                                        runRandomizer();
                                                    } else {
                                                        JSONObject serviceInfo = new JSONObject();
                                                        JSONArray results = response.getJSONArray("results");
                                                        Integer repCount = Integer.parseInt(response.getString("count"));
                                                        String latLong = latitude + "," + longitude;

                                                        serviceInfo.put("RESULTS", results);
                                                        serviceInfo.put("LOCATION", latLong);
                                                        serviceInfo.put("REP_COUNT", repCount);

                                                        Intent wearIntent = new Intent(getApplicationContext(), TwitterAuthenticateService.class);
                                                        wearIntent.putExtra("LOCATION", latLong);
                                                        wearIntent.putExtra("REP_COUNT", repCount);
                                                        wearIntent.putExtra("REP_RESULTS", results.toString());
                                                        wearIntent.putExtra("SERVICE_INFO", serviceInfo.toString());

                                                        wearIntent.putExtra("SHAKE_COUNTY", countyName + ", " + stateName);
                                                        startService(wearIntent);
                                                    }
                                                } catch (JSONException e) {

                                                }
                                            }
                                        }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // TODO Auto-generated method stub

                                            }
                                        });

                                MySingleton.getInstance(getBaseContext()).addToRequestQueue(jsObjRequest);
                            } catch (JSONException e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub

                        }
                    });

            MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } catch (JSONException e) {

        }
    }
}
