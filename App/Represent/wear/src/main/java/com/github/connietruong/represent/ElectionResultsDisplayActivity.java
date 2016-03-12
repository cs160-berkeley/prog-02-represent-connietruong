package com.github.connietruong.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ElectionResultsDisplayActivity extends Activity {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_results);
        Typeface meiryo = Typeface.createFromAsset(getAssets(), "fonts/meiryob.ttc");
        try {
            Intent intent = getIntent();
            TextView county_name = (TextView) findViewById(R.id.county_name);
            String county = intent.getStringExtra("COUNTY");
            county_name.setTypeface(meiryo);
            county_name.setText(county);

            Typeface euphemia = Typeface.createFromAsset(getAssets(), "fonts/Euphemia.ttf");
            TextView vote = (TextView) findViewById(R.id.vote);
            TextView in = (TextView) findViewById(R.id.in);
            TextView obama = (TextView) findViewById(R.id.obama);
            TextView romney = (TextView) findViewById(R.id.romney);
            vote.setTypeface(euphemia);
            in.setTypeface(euphemia);
            obama.setTypeface(euphemia);
            romney.setTypeface(euphemia);

            JSONObject percentages = new JSONObject(intent.getStringExtra("PERCENTAGES"));
            Integer obama_p = percentages.getInt("obama");
            Integer romney_p = percentages.getInt("romney");

            if (obama_p >= romney_p) {
                county_name.setTextColor(Color.parseColor("#004292"));
            } else {
                county_name.setTextColor(Color.parseColor("#920000"));
            }

            TextView obama_percent = (TextView) findViewById(R.id.obama_percent);
            TextView romney_percent = (TextView) findViewById(R.id.romney_percent);
            obama_percent.setTypeface(euphemia);
            obama_percent.setText(Integer.toString(obama_p) + "%");
            romney_percent.setTypeface(euphemia);
            romney_percent.setText(Integer.toString(romney_p) + "%");

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

                @Override
                public void onShake(int count) {
                    Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
                    intent.putExtra("SHAKE", "");
                    startService(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), "Shake Sent!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d("d", "Shake started");
                }
            });
        } catch (JSONException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }
}
