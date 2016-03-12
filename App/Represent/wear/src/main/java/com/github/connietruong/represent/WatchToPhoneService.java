package com.github.connietruong.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.ArrayList;


public class WatchToPhoneService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mWatchApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("T", "in onconnected");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            final String input;
            final String path;
            ArrayList<String> keys = new ArrayList<String>();
            if (extras.containsKey("col_number")) {
                path = "COL_NUMBER";
                keys.add(extras.getString("col_number"));
                if (extras.containsKey("ZIP_CODE")) {
                    keys.add(extras.getString("ZIP_CODE"));
                    keys.add("");
                } else {
                    keys.add("");
                    keys.add(extras.getString("LOCATION"));
                }
                if (extras.containsKey("SHAKE_COUNTY")) {
                    keys.add(extras.getString("SHAKE_COUNTY"));
                } else {
                    keys.add("");
                }

                Gson gson = new Gson();
                input = gson.toJson(keys);
            } else {
                path = "SHAKE";
                input = extras.getString("SHAKE");
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mWatchApiClient.connect();
                    sendMessage("/" + path, input);
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public void onConnectionSuspended(int i) {}

    private void sendMessage(final String path, final String text ) {
        Thread thread = new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mWatchApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    Log.d("T", "running sendMessage");
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mWatchApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        });
        thread.start();
    }

}
