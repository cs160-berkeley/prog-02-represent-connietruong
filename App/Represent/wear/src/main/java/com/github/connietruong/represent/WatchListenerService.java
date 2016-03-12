package com.github.connietruong.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Created by Connie Truong 2/26/16
 */
public class WatchListenerService extends WearableListenerService {
    final String ZIP_CODE = "/ZIP_CODE";
    final String LOCATION = "/LOCATION";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());

        if( messageEvent.getPath().equalsIgnoreCase( ZIP_CODE ) ) {
            final String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            final Intent intent = new Intent(this, MyDisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Log.d("T", "about to start watch MainActivity with ZIP_CODE input");
                try {
                    JSONObject strResults = new JSONObject(value);
                    String results = strResults.getJSONArray("RESULTS").toString();
                    intent.putExtra("ZIP_CODE", strResults.toString());
                    intent.putExtra("RESULTS", results);
                    startActivity(intent);
                } catch (JSONException e) {

                }
        } else if (messageEvent.getPath().equalsIgnoreCase( LOCATION )) {
            final String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            final Intent intent = new Intent(this, MyDisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Log.d("T", "about to start watch MainActivity with LOCATION input");
            try {
                JSONObject strResults = new JSONObject(value);
                String results = strResults.getJSONArray("RESULTS").toString();
                intent.putExtra("LOCATION", strResults.toString());
                intent.putExtra("RESULTS", results);
                startActivity(intent);
            } catch (JSONException e) {

            }
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}