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
 * Created by Connie on 3/9/2016.
 */
public class GeocodeCountyFindService extends Service {
    private String geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
    private String geocodeApiKey = "AIzaSyAnvSjJX5-QrpqDFx-smcrUdKw4sUCHjC4";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        if (intent != null) {
            try {
                final JSONObject serviceInfo = new JSONObject(intent.getStringExtra("SERVICE_INFO"));

                final String rep_result = intent.getStringExtra("REP_RESULTS");
                final Integer repCount = intent.getIntExtra("REP_COUNT", 3);
                final String twitter = intent.getStringExtra("TWITTER");

                final JSONArray results = new JSONArray(rep_result);
                if (intent.hasExtra("ZIP_CODE")) {
                    final String zip_code = intent.getStringExtra("ZIP_CODE");
                    String zipHttpGeocode = geocodeUrl + "address=" + zip_code + "&key=" + geocodeApiKey;
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, zipHttpGeocode, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray address_components = response.getJSONArray("results")
                                                .getJSONObject(0).getJSONArray("address_components");
                                        String county = "";
                                        String state = "";
                                        String state_long = "";
                                        for (int i = 0; i < address_components.length(); i++) {
                                            JSONObject component = address_components.getJSONObject(i);
                                            String type = component.getJSONArray("types").getString(0);
                                            if (type.equals("administrative_area_level_2")) {
                                                county = component.getString("short_name");
                                            } else if (type.equals("administrative_area_level_1")) {
                                                if (county.isEmpty()) {
                                                    county = component.getString("long_name");
                                                }
                                                state_long = component.getString("long_name");
                                                state = component.getString("short_name");
                                            }
                                        }
                                        county = county.split(" County")[0];
                                        serviceInfo.put("COUNTY", county + ", " + state);

                                        InputStream stream = getAssets().open("election-county-2012.json");
                                        int size = stream.available();
                                        byte[] buffer = new byte[size];
                                        stream.read(buffer);
                                        stream.close();
                                        JSONArray electionResults = new JSONArray(new String(buffer, "UTF-8"));
                                        JSONObject electionPercentage = new JSONObject();
                                        int stateO = 0;
                                        int stateR = 0;
                                        for (int i = 0; i < electionResults.length(); i++) {
                                            JSONObject curCounty = electionResults.getJSONObject(i);
                                            String state_postal = curCounty.getString("state-postal");
                                            String county_name = curCounty.getString("county-name");
                                            if (state_postal.equals(state)) {
                                                if (county_name.equals(county)) {
                                                    electionPercentage.put("obama", curCounty.getInt("obama-percentage"));
                                                    electionPercentage.put("romney", curCounty.getInt("romney-percentage"));
                                                } else if (county_name.equals(state_long)) {
                                                    stateO = curCounty.getInt("obama-percentage");
                                                    stateR = curCounty.getInt("romney-percentage");
                                                }
                                            }
                                        }
                                        if (electionPercentage.isNull("obama")) {
                                            electionPercentage.put("obama", stateO);
                                            electionPercentage.put("romney", stateR);
                                        }

                                        serviceInfo.put("PERCENTAGES", electionPercentage);

                                        Intent zipCodeServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                                        zipCodeServiceIntent.putExtra("ZIP_CODE", serviceInfo.toString());
                                        startService(zipCodeServiceIntent);

                                        Intent zipCodeIntent = new Intent(getBaseContext(), RepDisplayActivity.class);
                                        zipCodeIntent.putExtra("ZIP_CODE", zip_code);
                                        zipCodeIntent.putExtra("REP_COUNT", repCount);
                                        zipCodeIntent.putExtra("REP_RESULTS", results.toString());
                                        zipCodeIntent.putExtra("TWITTER", twitter);

                                        if (!serviceInfo.isNull("SHAKE_COUNTY")) {
                                            zipCodeIntent.putExtra("SHAKE_COUNTY", serviceInfo.getString("SHAKE_COUNTY"));
                                        }

                                        zipCodeIntent.addFlags(zipCodeIntent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(zipCodeIntent);
                                    } catch (JSONException e) {

                                    } catch (IOException e) {

                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
                } else {
                    final String location = intent.getStringExtra("LOCATION");
                    final Intent curIntent = intent;
                    String zipHttpGeocode = geocodeUrl + "address=" + location + "&key=" + geocodeApiKey;
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, zipHttpGeocode, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray address_components = response.getJSONArray("results")
                                                .getJSONObject(0).getJSONArray("address_components");
                                        String county = "";
                                        String state = "";
                                        String state_long = "";
                                        for (int i = 0; i < address_components.length(); i++) {
                                            JSONObject component = address_components.getJSONObject(i);
                                            String type = component.getJSONArray("types").getString(0);
                                            if (type.equals("administrative_area_level_2")) {
                                                county = component.getString("short_name");
                                            } else if (type.equals("administrative_area_level_1")) {
                                                if (county.isEmpty()) {
                                                    county = component.getString("long_name");
                                                }
                                                state_long = component.getString("long_name");
                                                state = component.getString("short_name");
                                            }
                                        }
                                        county = county.split(" County")[0];
                                        serviceInfo.put("COUNTY", county + ", " + state);

                                        InputStream stream = getAssets().open("election-county-2012.json");
                                        int size = stream.available();
                                        byte[] buffer = new byte[size];
                                        stream.read(buffer);
                                        stream.close();
                                        JSONArray electionResults = new JSONArray(new String(buffer, "UTF-8"));
                                        JSONObject electionPercentage = new JSONObject();
                                        int stateO = 0;
                                        int stateR = 0;
                                        for (int i = 0; i < electionResults.length(); i++) {
                                            JSONObject curCounty = electionResults.getJSONObject(i);
                                            String state_postal = curCounty.getString("state-postal");
                                            String county_name = curCounty.getString("county-name");
                                            if (state_postal.equals(state)) {
                                                if (county_name.equals(county)) {
                                                    electionPercentage.put("obama", curCounty.getInt("obama-percentage"));
                                                    electionPercentage.put("romney", curCounty.getInt("romney-percentage"));
                                                } else if (county_name.equals(state_long)) {
                                                    stateO = curCounty.getInt("obama-percentage");
                                                    stateR = curCounty.getInt("romney-percentage");
                                                }
                                            }
                                        }
                                        if (electionPercentage.isNull("obama")) {
                                            electionPercentage.put("obama", stateO);
                                            electionPercentage.put("romney", stateR);
                                        }

                                        serviceInfo.put("PERCENTAGES", electionPercentage);

                                        Intent zipCodeServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                                        zipCodeServiceIntent.putExtra("LOCATION", serviceInfo.toString());
                                        startService(zipCodeServiceIntent);

                                        Intent locationIntent = new Intent(getBaseContext(), RepDisplayActivity.class);
                                        locationIntent.putExtra("LOCATION", location);
                                        locationIntent.putExtra("REP_COUNT", repCount);
                                        locationIntent.putExtra("REP_RESULTS", results.toString());
                                        locationIntent.putExtra("TWITTER", twitter);

                                        if (!serviceInfo.isNull("SHAKE_COUNTY")) {
                                            locationIntent.putExtra("SHAKE_COUNTY", serviceInfo.getString("SHAKE_COUNTY"));
                                        }

                                        locationIntent.addFlags(locationIntent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(locationIntent);
                                    } catch (JSONException e) {

                                    } catch (IOException e) {

                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });
                    MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
                }

            } catch (JSONException e) {

            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
