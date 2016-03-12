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
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
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

        Intent intent = getIntent();

        try {
            JSONArray results = new JSONArray(intent.getStringExtra("RESULTS"));

            final GridPagerAdapter gridAdapter = new GridPagerAdapter(getBaseContext(), getFragmentManager(), results);
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
                    Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
                    intent.putExtra("SHAKE", "");
                    startService(intent);
                    Toast toast = Toast.makeText(getApplicationContext(), "Shake Sent!", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d("d", "Shake started");
                }
            });
            mShakeDetector.setStartupTimestamp(System.currentTimeMillis());
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

    class GridPagerAdapter extends FragmentGridPagerAdapter {
        private final Context mContext;
        private List mRows;
        private Page[] pages;
        private Page[][] PAGES = new Page[1][];

        public GridPagerAdapter(Context ctx, FragmentManager fm, JSONArray results) {
            super(fm);
            mContext = ctx;
            pages = new Page[results.length()];
            try {
                for (int i = 0; i < results.length(); i++) {
                    JSONObject representative = results.getJSONObject(i);
                    String sen_or_rep;
                    if (representative.getString("chamber").equals("house")) {
                        sen_or_rep = "Rep.";
                    } else {
                        sen_or_rep = "Sen.";
                    }
                    String name = representative.getString("first_name") + " " + representative.getString("last_name");
                    String party = representative.getString("party");
                    String rep_party;
                    String image_url = "https://theunitedstates.io/images/congress/450x550/" +
                            representative.getString("bioguide_id") + ".jpg";
                    String twitter_id = representative.getString("twitter_id");

                    if (party.equals("D")) {
                        //make twitter api call here????
                        rep_party = "Democrat";
                    } else if (party.equals("R")) {
                        rep_party = "Republican";
                    } else {
                        rep_party = "Independent";
                    }
                    Page newPage = new Page(sen_or_rep, name, rep_party, image_url);
                    Array.set(pages, i, newPage);
                }
                Array.set(PAGES, 0, pages);
            } catch (JSONException e) {

            }
        }

        // A simple container for static data in each page
        private class Page {
            // static resources
            String sen_or_rep;
            String rep_name;
            String rep_party;
            String image = "";
            private Page (String sen_or_rep, String rep_name, String rep_party, String image_url) {
                this.sen_or_rep = sen_or_rep;
                this.rep_name = rep_name;
                this.rep_party = rep_party;
                image = image_url;
            }

        }

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

