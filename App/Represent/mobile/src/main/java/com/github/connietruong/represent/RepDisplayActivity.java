package com.github.connietruong.represent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class RepDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_bar_icon);
        getSupportActionBar().setElevation(0);
        Typeface droidSans = Typeface.createFromAsset(getAssets(), "fonts/DroidSans.ttf");

        LinearLayout repLayout = (LinearLayout) findViewById(R.id.location_or_zip);
        repLayout.setElevation(0);
        Intent intent = getIntent();
        if (intent.hasExtra("ZIP_CODE")) {
            TextView basedOn = new TextView(this);
            basedOn.setTypeface(droidSans);
            basedOn.setText("Based on ZIP:");
            basedOn.setTextColor(Color.parseColor("#474747"));
            repLayout.addView(basedOn);

            TextView zipCode = new TextView(this);
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
                                    //  Your code when user clicked on Cancel

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
                                    EditText newZip = (EditText) d.findViewById(R.id.new_zip);
                                    String zip_code = newZip.getText().toString();
                                    if (zip_code.length() == 5) {
                                        // add service too
                                        Intent ser_intent = new Intent(getBaseContext(), PhoneToWatchService.class);
                                        ser_intent.putExtra("ZIP_CODE", zip_code);
                                        startService(ser_intent);

                                        Intent intent = new Intent(getBaseContext(), RepDisplayActivity.class);
                                        intent.putExtra("ZIP_CODE", zip_code);
                                        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);
                                        d.dismiss();
                                    } else {
                                        AlertDialog d;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                        builder.setTitle("Error: Invalid ZIP Code");
                                        builder.setMessage("Please enter a valid ZIP code.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        d = builder.create();
                                        d.show();
                                    }
                                }
                            });
                        }
                    });
                    d.show();
                }
            });
            repLayout.addView(changeButton);
        } else {
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
                public void onClick(View v) {
                    //access gps and set to new location
                    Intent serIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                    serIntent.putExtra("LOCATION", "placeholder");
                    startService(serIntent);

                    Intent intent = new Intent(getBaseContext(), RepDisplayActivity.class);
                    intent.putExtra("LOCATION", "placeholder");
                    startActivity(intent);
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
        startActivity(backIntent);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        Integer col_number = -1;

        @Override
        public Fragment getItem(int position) {
            return RepresentativesFragment.newInstance(position, col_number);
        }

        public void setColNumber(int v) {
            col_number = v;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
