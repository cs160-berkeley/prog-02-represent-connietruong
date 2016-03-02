package com.github.connietruong.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class ElectionResultsDisplayActivity extends Activity {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election_results);
        Typeface meiryo = Typeface.createFromAsset(getAssets(), "fonts/meiryob.ttc");
        TextView county_name = (TextView) findViewById(R.id.county_name);
        county_name.setTypeface(meiryo);

        Typeface euphemia = Typeface.createFromAsset(getAssets(), "fonts/Euphemia.ttf");
        TextView vote = (TextView) findViewById(R.id.vote);
        TextView in = (TextView) findViewById(R.id.in);
        TextView obama = (TextView) findViewById(R.id.obama);
        TextView romney = (TextView) findViewById(R.id.romney);
        vote.setTypeface(euphemia);
        in.setTypeface(euphemia);
        obama.setTypeface(euphemia);
        romney.setTypeface(euphemia);

        TextView obama_percent = (TextView) findViewById(R.id.obama_percent);
        TextView romney_percent = (TextView) findViewById(R.id.romney_percent);
        obama_percent.setTypeface(euphemia);
        romney_percent.setTypeface(euphemia);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * Here is where we would get a random valid zip code
				 */
                String randomZip = Integer.toString(90000 + (int) (Math.random() * ((99999 - 90000) + 1)));
                Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
                intent.putExtra("ZIP_CODE", randomZip);
                startService(intent);
            }
        });
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
