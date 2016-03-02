package com.github.connietruong.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by Connie Truong 2/26/16
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    final String ZIP_CODE = "/ZIP_CODE";
    final String LOCATION = "/LOCATION";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());

        if( messageEvent.getPath().equalsIgnoreCase( ZIP_CODE ) ) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MyDisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //you need to add this flag since you're starting a new activity from a service

            //maybe if i'm forced to make api call twice, put it here??
            Log.d("T", "about to start watch MainActivity with ZIP_CODE input");
            intent.putExtra("ZIP_CODE", value);
            startActivity(intent);
        } else if (messageEvent.getPath().equalsIgnoreCase( LOCATION )) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MyDisplayActivity.class );
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //you need to add this flag since you're starting a new activity from a service
            Log.d("T", "about to start watch MainActivity with LOCATION input");
            intent.putExtra("LOCATION", value);
            startActivity(intent);
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}