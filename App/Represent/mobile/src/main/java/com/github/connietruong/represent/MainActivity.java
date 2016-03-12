package com.github.connietruong.represent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final String sunlightApiKey = "100a77ba6f7e460b9ff1ac18a3e24113";
    private final String sunlightUrl = "http://congress.api.sunlightfoundation.com/legislators/locate";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.app_bar_icon);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (findViewById(R.id.buttonsFragmentContainer) != null) {

            if (savedInstanceState != null) {
                return;
            }

            ButtonsFragment defaultFragment = new ButtonsFragment();

            defaultFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.buttonsFragmentContainer, defaultFragment).commit();
        }
    }

    public void useZip(View view) {
        ZipCodeFragment zipFragment = new ZipCodeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.buttonsFragmentContainer, zipFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void findRepsZip(View view) {
        EditText enteredZip = (EditText) findViewById(R.id.zip_code);
        final String zipCode = enteredZip.getText().toString();
        final TextView error = (TextView) findViewById(R.id.invalid_zip);
        String zipHttpCall = sunlightUrl + "?zip=" + zipCode + "&apikey=" + sunlightApiKey;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, zipHttpCall, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final Integer repCount = Integer.parseInt(response.getString("count"));
                            if (repCount == 0) {
                                error.setText("Please enter a valid ZIP code.");
                            } else {
                                final JSONArray results = response.getJSONArray("results");

                                final JSONObject serviceInfo = new JSONObject();
                                serviceInfo.put("RESULTS", results);
                                serviceInfo.put("ZIP_CODE", zipCode);
                                serviceInfo.put("REP_COUNT", repCount);

                                Intent zipCodeIntent = new Intent(getBaseContext(), TwitterAuthenticateService.class);
                                zipCodeIntent.putExtra("COUNTER", 0);
                                zipCodeIntent.putExtra("ZIP_CODE", zipCode);
                                zipCodeIntent.putExtra("REP_COUNT", repCount);
                                zipCodeIntent.putExtra("REP_RESULTS", results.toString());
                                zipCodeIntent.putExtra("SERVICE_INFO", serviceInfo.toString());

                                zipCodeIntent.addFlags(zipCodeIntent.getFlags());
                                startService(zipCodeIntent);
                                error.setText("Loading results...");
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
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        error.setText("Checking if input is valid ZIP code...");
    }

    public void findRepsLocation(View view) {
        mGoogleApiClient.connect();
        final TextView error = (TextView) findViewById(R.id.loading);
        error.setText("Loading Results...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                final String latitude = String.valueOf(mLastLocation.getLatitude());
                final String longitude = String.valueOf(mLastLocation.getLongitude());
                String locationHttpCall = sunlightUrl + "?latitude=" + latitude +
                        "&longitude=" + longitude + "&apikey=" + sunlightApiKey;
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, locationHttpCall, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject serviceInfo = new JSONObject();
                                    JSONArray results = response.getJSONArray("results");
                                    Integer repCount = Integer.parseInt(response.getString("count"));
                                    String latLong = latitude + "," + longitude;

                                    serviceInfo.put("RESULTS", results);
                                    serviceInfo.put("LOCATION", latLong);
                                    serviceInfo.put("REP_COUNT", repCount);

                                    Intent locationIntent = new Intent(getBaseContext(), TwitterAuthenticateService.class);
                                    locationIntent.putExtra("LOCATION", latLong);
                                    locationIntent.putExtra("REP_COUNT", repCount);
                                    locationIntent.putExtra("REP_RESULTS", results.toString());
                                    locationIntent.putExtra("SERVICE_INFO", serviceInfo.toString());

                                    locationIntent.setFlags(locationIntent.getFlags());
                                    locationIntent.addFlags(locationIntent.getFlags());
                                    startService(locationIntent);
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
            } else {
                AlertDialog d;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error: GPS not available");
                builder.setMessage("Please turn on GPS to access this function.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                d = builder.create();
                d.show();
            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {
    }

}
