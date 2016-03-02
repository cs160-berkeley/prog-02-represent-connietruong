package com.github.connietruong.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.app_bar_icon);

        if (findViewById(R.id.buttonsFragmentContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ButtonsFragment defaultFragment = new ButtonsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            defaultFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.buttonsFragmentContainer, defaultFragment).commit();
        }
    }

    public void useZip(View view) {
        ZipCodeFragment zipFragment = new ZipCodeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right, 0, 0);
        transaction.replace(R.id.buttonsFragmentContainer, zipFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void findRepsZip(View view) {
        EditText enteredZip = (EditText) findViewById(R.id.zip_code);
        String zipCode = enteredZip.getText().toString();
        TextView error = (TextView) findViewById(R.id.invalid_zip);
        //check if Zipcode is valid or not
        if (zipCode.length() != 5) {
            error.setText("Please enter 5 digits for ZIP code.");
        } else {
            Intent zipCodeServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            zipCodeServiceIntent.putExtra("ZIP_CODE", zipCode);
            startService(zipCodeServiceIntent);

            Intent zipCodeIntent = new Intent(getBaseContext(), RepDisplayActivity.class);
            zipCodeIntent.putExtra("ZIP_CODE", zipCode);
            zipCodeIntent.setFlags(zipCodeIntent.getFlags());
            startActivity(zipCodeIntent);
        }
    }

    public void findRepsLocation(View view) {
        //use GPS to find location
        // Acquire a reference to the system Location Manager

        Intent zipCodeServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        zipCodeServiceIntent.putExtra("LOCATION", "placeholder");
        startService(zipCodeServiceIntent);

        Intent locationIntent = new Intent(getBaseContext(), RepDisplayActivity.class);
        //Gson gson = new Gson();
        locationIntent.putExtra("LOCATION", "placeholder");
        locationIntent.setFlags(locationIntent.getFlags());
        startActivity(locationIntent);

    }

}
