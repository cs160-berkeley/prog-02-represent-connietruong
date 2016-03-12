package com.github.connietruong.represent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RepDisplayActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Integer REP_COUNT = 3;
    String TWITTER = "";
    final String sunlightApiKey = "100a77ba6f7e460b9ff1ac18a3e24113";
    final String sunlightUrl = "http://congress.api.sunlightfoundation.com/legislators/locate";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.app_bar_icon);
        getSupportActionBar().setElevation(0);
        Typeface droidSans = Typeface.createFromAsset(getAssets(), "fonts/DroidSans.ttf");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        LinearLayout repLayout = (LinearLayout) findViewById(R.id.location_or_zip);
        repLayout.setElevation(0);
        Intent intent = getIntent();
        TWITTER = intent.getStringExtra("TWITTER");

        if (intent.hasExtra("SHAKE_COUNTY")) {
            TextView basedOn = new TextView(this);
            basedOn.setTypeface(droidSans);
            basedOn.setText("Based on random location:");
            basedOn.setTextColor(Color.parseColor("#474747"));
            repLayout.addView(basedOn);

            TextView county = new TextView(this);
            county.setTypeface(droidSans, 1);
            county.setTextColor(Color.parseColor("#292929"));
            county.setText(intent.getStringExtra("SHAKE_COUNTY"));
            county.setPadding(55, 0, 0, 0);
            repLayout.addView(county);
        } else if (intent.hasExtra("ZIP_CODE")) {
            REP_COUNT = intent.getIntExtra("REP_COUNT", 3);

            TextView basedOn = new TextView(this);
            basedOn.setTypeface(droidSans);
            basedOn.setText("Based on ZIP:");
            basedOn.setTextColor(Color.parseColor("#474747"));
            repLayout.addView(basedOn);

            final TextView zipCode = new TextView(this);
            zipCode.setTypeface(droidSans, 1);
            zipCode.setTextColor(Color.parseColor("#303030"));
            zipCode.setText(intent.getStringExtra("ZIP_CODE"));
            zipCode.setPadding(25, 0, 0, 0);
            repLayout.addView(zipCode);

            TextView changeButton = new TextView(this);
            changeButton.setTypeface(droidSans, 1);
            changeButton.setTextColor(Color.parseColor("#2077ED"));
            changeButton.setText("Change");
            changeButton.setPadding(55, 0, 0, 0);
            changeButton.setClickable(true);
            changeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog d;
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Enter new ZIP code:");
                    final EditText input = new EditText(v.getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    input.setId(R.id.new_zip);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);

                    builder.setView(input)
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    d = builder.create();
                    d.setOnShowListener(new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(DialogInterface dialog) {

                            Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                            b.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    final EditText newZip = (EditText) d.findViewById(R.id.new_zip);
                                    final String zip_code = newZip.getText().toString();
                                    final View view2 = view;
                                    String zipHttpCall = sunlightUrl + "?zip=" + zip_code + "&apikey=" + sunlightApiKey;
                                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                            (Request.Method.GET, zipHttpCall, null, new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        Integer repCount = Integer.parseInt(response.getString("count"));
                                                        if (repCount == 0) {
                                                            AlertDialog d;
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(view2.getContext());
                                                            builder.setTitle("Error: Invalid ZIP Code");
                                                            builder.setMessage("Please enter a valid ZIP code.");
                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                }
                                                            });
                                                            d = builder.create();
                                                            d.show();
                                                        } else {
                                                            JSONArray repResults = response.getJSONArray("results");

                                                            JSONObject serviceInfo = new JSONObject();
                                                            serviceInfo.put("RESULTS", repResults);
                                                            serviceInfo.put("ZIP_CODE", zip_code);
                                                            serviceInfo.put("REP_COUNT", repCount);

                                                            Intent intent = new Intent(getBaseContext(), TwitterAuthenticateService.class);
                                                            intent.putExtra("ZIP_CODE", zip_code);
                                                            intent.putExtra("REP_COUNT", repCount);
                                                            intent.putExtra("REP_RESULTS", repResults.toString());
                                                            intent.putExtra("SERVICE_INFO", serviceInfo.toString());
                                                            startService(intent);
                                                            d.dismiss();
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
                                }
                            });
                        }
                    });
                    d.show();
                }
            });
            repLayout.addView(changeButton);
        }  else {
            REP_COUNT = intent.getIntExtra("REP_COUNT", 3);

            TextView basedOn = new TextView(this);
            basedOn.setTypeface(droidSans);
            basedOn.setText("Based on your location:");
            basedOn.setTextColor(Color.parseColor("#474747"));
            repLayout.addView(basedOn);

            TextView refreshButton = new TextView(this);
            refreshButton.setTypeface(droidSans, 1);
            refreshButton.setTextColor(Color.parseColor("#2077ED"));
            refreshButton.setText("Refresh");
            refreshButton.setPadding(55, 0, 0, 0);
            refreshButton.setClickable(true);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoogleApiClient.connect();
                }
            });
            repLayout.addView(refreshButton);
        }

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        final ScreenSlidePagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        if (intent.hasExtra("COL_NUMBER")) {
            int col_number = Integer.parseInt(intent.getStringExtra("COL_NUMBER"));
            mPagerAdapter.setColNumber(col_number);
        }
        mPagerAdapter.setRepResults(intent.getStringExtra("REP_RESULTS"));

        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout pageCircles = (LinearLayout) findViewById(R.id.page_circles);
                if (pageCircles.getChildCount() == 0) {
                    for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                        ImageView dot = new ImageView(getBaseContext());
                        dot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (i == position) {
                            dot.setBackgroundResource(R.drawable.circle_selected);
                            dot.getLayoutParams().height = 32;
                            dot.getLayoutParams().width = 32;
                        } else {
                            dot.setBackgroundResource(R.drawable.circle_unselected);
                            dot.getLayoutParams().height = 20;
                            dot.getLayoutParams().width = 20;
                        }
                        pageCircles.addView(dot);

                        LinearLayout paddingLayout = new LinearLayout(getBaseContext());
                        paddingLayout.setPadding(10, 0, 10, 0);
                        pageCircles.addView(paddingLayout);
                    }
                } else {
                    for (int i = 0; i < pageCircles.getChildCount(); i += 2) {
                        ImageView dot = (ImageView) pageCircles.getChildAt(i);
                        dot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (i / 2 == position) {
                            dot.setBackgroundResource(R.drawable.circle_selected);
                            dot.getLayoutParams().height = 32;
                            dot.getLayoutParams().width = 32;
                        } else {
                            dot.setBackgroundResource(R.drawable.circle_unselected);
                            dot.getLayoutParams().height = 20;
                            dot.getLayoutParams().width = 20;
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (intent.hasExtra("COL_NUMBER")) {
            int col_number = Integer.parseInt(intent.getStringExtra("COL_NUMBER"));
            mPager.setCurrentItem(col_number);
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(backIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        Integer col_number = -1;
        String repResults = null;

        @Override
        public Fragment getItem(int position) {
            return RepresentativesFragment.newInstance(position, col_number, repResults, TWITTER);
        }

        public void setColNumber(int v) {
            col_number = v;
        }

        public void setRepResults(String r) {
            repResults = r;
        }

        @Override
        public int getCount() {
            return REP_COUNT;
        }
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
