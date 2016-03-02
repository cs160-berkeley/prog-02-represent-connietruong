package com.github.connietruong.represent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

public class MyDisplayActivity extends Activity {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        GridViewPager gridPager = (GridViewPager) findViewById(R.id.pager);
        final GridPagerAdapter gridAdapter = new GridPagerAdapter(this, getFragmentManager());
        gridPager.setAdapter(gridAdapter);
        gridPager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, int i1, float v, float v1, int i2, int i3) {
                int totalCol = gridAdapter.getColumnCount(i);
                LinearLayout pageCircles = (LinearLayout) findViewById(R.id.page_circles);
                if (pageCircles.getChildCount() == 0) {
                    for (int j = 0; j < totalCol; j++) {
                        ImageView dot = new ImageView(getBaseContext());
                        dot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (j == i1) {
                            dot.setBackgroundResource(R.drawable.circle_selected);
                            dot.getLayoutParams().height = 10;
                            dot.getLayoutParams().width = 10;
                        } else {
                            dot.setBackgroundResource(R.drawable.circle_unselected);
                            dot.getLayoutParams().height = 7;
                            dot.getLayoutParams().width = 7;
                        }
                        pageCircles.addView(dot);

                        LinearLayout paddingLayout = new LinearLayout(getBaseContext());
                        paddingLayout.setPadding(3, 0, 3, 0);
                        pageCircles.addView(paddingLayout);
                    }
                } else {
                    for (int j = 0; j < pageCircles.getChildCount(); j += 2) {
                        ImageView dot = (ImageView) pageCircles.getChildAt(j);
                        dot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        if (j / 2 == i1) {
                            dot.setBackgroundResource(R.drawable.circle_selected);
                            dot.getLayoutParams().height = 10;
                            dot.getLayoutParams().width = 10;
                        } else {
                            dot.setBackgroundResource(R.drawable.circle_unselected);
                            dot.getLayoutParams().height = 7;
                            dot.getLayoutParams().width = 7;
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int i, int i1) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * Here is where we would get a random valid zip code
				 */
                String randomZip = Integer.toString(90000 + (int)(Math.random() * ((99999 - 90000) + 1)));
                Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
                intent.putExtra("ZIP_CODE", randomZip);
                startService(intent);
            }
        });
        mShakeDetector.setStartupTimestamp(System.currentTimeMillis());
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

    class GridPagerAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;
        private List mRows;

        public GridPagerAdapter(Context ctx, FragmentManager fm) {
            super(fm);
            mContext = ctx;
        }

        final int[] BG_IMAGES = new int[] {};

        // A simple container for static data in each page
        private class Page {
            // static resources
            String sen_or_rep;
            String rep_name;
            String rep_party;
            Integer image = 0;
            private Page (String sen_or_rep, String rep_name, String rep_party, Integer image_id) {
                this.sen_or_rep = sen_or_rep;
                this.rep_name = rep_name;
                this.rep_party = rep_party;
                image = image_id;
            }

        }

        Page feinstein = new Page("Sen.", "Dianne Feinstein", "Democrat", R.drawable.feinstein);
        Page boxer = new Page("Sen.", "Barbara Boxer", "Democrat", R.drawable.boxer);
        Page lee = new Page("Rep.", "Barbara Lee", "Democrat", R.drawable.lee);


        // Create a static set of pages in a 2D array
        private final Page[][] PAGES = {{feinstein, boxer, lee}};

        // Obtain the UI fragment at the specified position
        @Override
        public Fragment getFragment(int row, int col) {
            Page page = PAGES[row][col];

            Intent intent = getIntent();
            if (intent.hasExtra("ZIP_CODE")) {
                return RepresentativeDisplayFragment.newInstance(page.sen_or_rep, page.rep_name,
                        page.rep_party, page.image, col, intent.getStringExtra("ZIP_CODE"), "");
            }
            return RepresentativeDisplayFragment.newInstance(page.sen_or_rep, page.rep_name,
                    page.rep_party, page.image, col, "", intent.getStringExtra("LOCATION"));
        }

        // Obtain the background image for the row
        @Override
        public Drawable getBackgroundForRow(int row) {
            return GridPagerAdapter.BACKGROUND_NONE;
        }

        // Obtain the background image for the specific page
        @Override
        public Drawable getBackgroundForPage(int row, int column) {
            return GridPagerAdapter.BACKGROUND_NONE;
        }

        // Obtain the number of pages (vertical)
        @Override
        public int getRowCount() {
            return PAGES.length;
        }

        // Obtain the number of pages (horizontal)
        @Override
        public int getColumnCount(int rowNum) {
            return PAGES[rowNum].length;
        }
    }
}

