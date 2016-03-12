package com.github.connietruong.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Connie Truong 2/26/2016
 */
public class PhoneListenerService extends WearableListenerService {

    private static final String COLUMN_NUMBER = "/COL_NUMBER";
    private static final String SHAKE = "/SHAKE";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(COLUMN_NUMBER) ) {

            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            ArrayList<String> strings = gson.fromJson(value, new TypeToken<ArrayList<String>>() {
            }.getType());

            final Intent intent = new Intent(getApplicationContext(), RepDisplayActivity.class);
            intent.putExtra("COL_NUMBER", strings.get(0));
            if (strings.get(1).isEmpty()) {
                final String location = strings.get(2);
                try {
                    JSONObject serviceExtras = new JSONObject(location);
                    JSONArray results = serviceExtras.getJSONArray("RESULTS");
                    String latLong = serviceExtras.getString("LOCATION");
                    String rep_count = serviceExtras.getString("REP_COUNT");
                    String twitter = serviceExtras.getString("TWITTER");
                    intent.putExtra("TWITTER", twitter);
                    intent.putExtra("REP_COUNT", rep_count);
                    intent.putExtra("LOCATION", latLong);
                    intent.putExtra("REP_RESULTS", results.toString());

                    if (!serviceExtras.isNull("SHAKE_COUNTY")) {
                        intent.putExtra("SHAKE_COUNTY", serviceExtras.getString("SHAKE_COUNTY"));
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {

                }
            } else {
                final String zipCode = strings.get(1);
                try {
                    JSONObject serviceExtras = new JSONObject(zipCode);
                    JSONArray results = serviceExtras.getJSONArray("RESULTS");
                    String zip_code = serviceExtras.getString("ZIP_CODE");
                    Integer rep_count = serviceExtras.getInt("REP_COUNT");
                    String twitter = serviceExtras.getString("TWITTER");
                    intent.putExtra("TWITTER", twitter);
                    intent.putExtra("REP_COUNT", rep_count);
                    intent.putExtra("ZIP_CODE", zip_code);
                    intent.putExtra("REP_RESULTS", results.toString());

                    if (!serviceExtras.isNull("SHAKE_COUNTY")) {
                        intent.putExtra("SHAKE_COUNTY", serviceExtras.getString("SHAKE_COUNTY"));
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {

                }
            }

        } else if (messageEvent.getPath().equalsIgnoreCase(SHAKE)) {
            Intent intent = new Intent(getBaseContext(), RandomizerService.class);
            startService(intent);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}
