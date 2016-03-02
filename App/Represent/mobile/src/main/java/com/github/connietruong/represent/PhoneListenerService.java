package com.github.connietruong.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by Connie Truong 2/26/2016
 */
public class PhoneListenerService extends WearableListenerService {

//   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String COLUMN_NUMBER = "/COL_NUMBER";
    private static final String ZIP_CODE = "/ZIP_CODE";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(COLUMN_NUMBER) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            ArrayList<String> strings = gson.fromJson(value, new TypeToken<ArrayList<String>>() {
            }.getType());

            Intent intent = new Intent(getApplicationContext(), RepDisplayActivity.class);
            intent.putExtra("COL_NUMBER", strings.get(0));
            if (strings.get(1).isEmpty()) {
                intent.putExtra("LOCATION", strings.get(2));
            } else {
                intent.putExtra("ZIP_CODE", strings.get(1));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else if (messageEvent.getPath().equalsIgnoreCase(ZIP_CODE)) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent wearIntent = new Intent(getApplicationContext(), PhoneToWatchService.class);
            wearIntent.putExtra("ZIP_CODE", value);
            startService(wearIntent);

            Intent intent = new Intent(getApplicationContext(), RepDisplayActivity.class);
            intent.putExtra("ZIP_CODE", value);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            super.onMessageReceived( messageEvent );
        }

    }
}
