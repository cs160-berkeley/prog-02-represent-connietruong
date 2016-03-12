package com.github.connietruong.represent;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Connie on 3/9/2016.
 */
public class TwitterAuthenticateService extends Service {
    private static final String TWITTER_KEY = "LmsFraFXJ9gInMneMYYGgIREo";
    private static final String TWITTER_SECRET = "3yyccRRUB0PMdB7GPwEd2fSzjILbZKb7NPgYzXoy7AxYXymfxx";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        if (intent != null) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
            Fabric.with(this, new Twitter(authConfig));

            final Intent intentF = intent;

            try {
                final JSONObject serviceInfo = new JSONObject(intent.getStringExtra("SERVICE_INFO"));
                final Integer currentI = intent.getIntExtra("COUNTER", 0);

                final String rep_result = intent.getStringExtra("REP_RESULTS");
                final Integer repCount = intent.getIntExtra("REP_COUNT", 3);

                JSONArray twitterJSON;
                if (intent.hasExtra("TWITTER_RESULTS")) {
                    twitterJSON = new JSONArray(intent.getStringExtra("TWITTER_RESULTS"));
                } else {
                    twitterJSON = new JSONArray();
                }

                final JSONArray results = new JSONArray(rep_result);
                final JSONArray twitterResults = twitterJSON;

                TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                    @Override
                    public void success(Result<AppSession> appSessionResult) {
                        try {
                            AppSession session = appSessionResult.data;
                            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                            final int finalI = results.length();
                            final JSONObject oneTwitter = new JSONObject();
                            String twitter_id = results.getJSONObject(currentI).getString("twitter_id");
                            if (twitter_id.equals("null")) {
                                twitterResults.put(oneTwitter);
                            } else {
                                twitterApiClient.getStatusesService().userTimeline(null, twitter_id, 1, null, null, false, false, false, true,
                                        new Callback<List<Tweet>>() {
                                            @Override
                                            public void success(Result<List<Tweet>> listResult) {
                                                try {
                                                    String tweet = listResult.data.get(0).text;
                                                    oneTwitter.put("TWEET", tweet);
                                                    twitterResults.put(currentI, oneTwitter);

                                                    if (twitterResults.length() == finalI) {
                                                        serviceInfo.put("TWITTER", twitterResults);

                                                        Intent zipCodeIntent = new Intent(getBaseContext(), GeocodeCountyFindService.class);
                                                        if (intentF.hasExtra("ZIP_CODE")) {
                                                            zipCodeIntent.putExtra("ZIP_CODE", intentF.getStringExtra("ZIP_CODE"));
                                                        } else {
                                                            zipCodeIntent.putExtra("LOCATION", intentF.getStringExtra("LOCATION"));
                                                        }

                                                        zipCodeIntent.putExtra("REP_COUNT", repCount);
                                                        zipCodeIntent.putExtra("REP_RESULTS", results.toString());
                                                        zipCodeIntent.putExtra("TWITTER", twitterResults.toString());
                                                        if (intentF.hasExtra("SHAKE_COUNTY")) {
                                                            serviceInfo.put("SHAKE_COUNTY", intentF.getStringExtra("SHAKE_COUNTY"));
                                                            zipCodeIntent.putExtra("SHAKE_COUNTY", intentF.getStringExtra("SHAKE_COUNTY"));
                                                        }
                                                        zipCodeIntent.putExtra("SERVICE_INFO", serviceInfo.toString());

                                                        startService(zipCodeIntent);
                                                    } else {
                                                        Intent twitterSerIntent = new Intent(getBaseContext(), TwitterAuthenticateService.class);
                                                        if (intentF.hasExtra("ZIP_CODE")) {
                                                            twitterSerIntent.putExtra("ZIP_CODE", intentF.getStringExtra("ZIP_CODE"));
                                                        } else {
                                                            twitterSerIntent.putExtra("LOCATION", intentF.getStringExtra("LOCATION"));
                                                        }
                                                        twitterSerIntent.putExtra("REP_COUNT", repCount);
                                                        twitterSerIntent.putExtra("REP_RESULTS", results.toString());
                                                        twitterSerIntent.putExtra("SERVICE_INFO", serviceInfo.toString());
                                                        if (intentF.hasExtra("SHAKE_COUNTY")) {
                                                            serviceInfo.put("SHAKE_COUNTY", intentF.getStringExtra("SHAKE_COUNTY"));
                                                            twitterSerIntent.putExtra("SHAKE_COUNTY", intentF.getStringExtra("SHAKE_COUNTY"));
                                                        }
                                                        twitterSerIntent.putExtra("COUNTER", currentI + 1);
                                                        twitterSerIntent.putExtra("TWITTER_RESULTS", twitterResults.toString());
                                                        startService(twitterSerIntent);
                                                    }
                                                } catch (JSONException e) {

                                                }
                                            }

                                            @Override
                                            public void failure(TwitterException e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }
                            //}
                        } catch (JSONException e) {

                        }

                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
            } catch (JSONException e) {

            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
