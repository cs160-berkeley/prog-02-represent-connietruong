package com.github.connietruong.represent;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormatSymbols;

import me.grantland.widget.AutofitTextView;

/**
 * Created by Connie on 2/25/2016.
 */
public class RepresentativesFragment extends Fragment {
    final String sunlightUrl = "http://congress.api.sunlightfoundation.com/";
    private final String sunlightApiKey = "100a77ba6f7e460b9ff1ac18a3e24113";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.representative_template_view, container, false);

        try {
            Typeface ebrima = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ebrima.ttf");
            TextView sen_or_rep = (TextView) v.findViewById(R.id.senator_or_rep);
            TextView rep_name = (TextView) v.findViewById(R.id.rep_name);
            TextView rep_party = (TextView) v.findViewById(R.id.rep_party);
            final TextView tweet_text = (TextView) v.findViewById(R.id.tweet_text);
            TextView tweet_id = (TextView) v.findViewById(R.id.tweet_id);
            Button website_button = (Button) v.findViewById(R.id.website_button);
            Button email_button = (Button) v.findViewById(R.id.email_button);
            TextView more_info = (TextView) v.findViewById(R.id.more_info_button);
            final ImageView rep_image = (ImageView) v.findViewById(R.id.rep_image);
            int position = getArguments().getInt("position");
            JSONArray repResults = new JSONArray(getArguments().getString("rep_results"));
            final JSONObject representative = repResults.getJSONObject(position);

            JSONObject twitterInfo = (new JSONArray(getArguments().getString("twitter"))).getJSONObject(position);

            sen_or_rep.setTypeface(ebrima);
            if (representative.getString("chamber").equals("house")) {
                sen_or_rep.setText("Representative");
            } else {
                sen_or_rep.setText("Senator");
            }

            String twitter_id = representative.getString("twitter_id");
            String party = representative.getString("party");

            new DownloadImageTask(rep_image, v, position,
                    representative.getString("bioguide_id"),
                    representative.getString("term_end")).execute("https://theunitedstates.io/images/congress/450x550/" +
                    representative.getString("bioguide_id") + ".jpg");

            tweet_text.setTypeface(ebrima);

            if (!twitter_id.equals("null")) {
                String tweet = twitterInfo.getString("TWEET");
                tweet = tweet.replace("&amp;", "&");
                tweet_text.setText("\"" + tweet + "\"");
                tweet_id.setTypeface(ebrima);
                tweet_id.setText("@" + twitter_id);


            } else {
                tweet_text.setText("This delegate currently does not have a Twitter set up. How unfortunate.");
            }

            rep_name.setTypeface(ebrima);
            rep_name.setText(representative.getString("first_name") + " " + representative.get("last_name"));

            rep_party.setTypeface(ebrima);
            if (party.equals("D")) {
                rep_party.setText("Democrat");
            } else if (party.equals("R")) {
                rep_party.setText("Republican");
                rep_image.setBackgroundResource(R.drawable.outline_republican);
                website_button.setBackgroundResource(R.drawable.rbutton_short);
                email_button.setBackgroundResource(R.drawable.rbutton_short);
                more_info.setTextColor(Color.parseColor("#F03C30"));
            } else {
                rep_party.setText("Independent");
                rep_image.setBackgroundResource(R.drawable.outline_independent);
                website_button.setBackgroundResource(R.drawable.ibutton_short);
                email_button.setBackgroundResource(R.drawable.ibutton_short);
                more_info.setTextColor(Color.parseColor("#B3B3B3"));
            }

            website_button.setTypeface(ebrima);
            email_button.setTypeface(ebrima);
            more_info.setTypeface(ebrima, Typeface.BOLD);

            website_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(representative.getString("website")));
                        startActivity(browserIntent);
                    } catch (JSONException e) {

                    }
                }
            });
            //change button background based on rep or dem or independent

            email_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(representative.getString("contact_form")));
                        startActivity(browserIntent);
                    } catch (JSONException e) {

                    }
                }
            });
            //change button background based on rep or dem or independent

            more_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String bioguide_id = representative.getString("bioguide_id");
                        String end = representative.getString("term_end");
                        inflateMoreInfo(bioguide_id, end, getView());
                    } catch (JSONException e) {

                    }
                }
            });


        } catch (JSONException e) {

        }

        return v;
    }

    public static RepresentativesFragment newInstance(int position, int col_number, String repResults, String twitter) {
        RepresentativesFragment instance = new RepresentativesFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putInt("col_number", col_number);
        b.putString("rep_results", repResults);
        b.putString("twitter", twitter);

        instance.setArguments(b);
        return instance;
    }

    public void inflateMoreInfo(String bioguide_id, String end, View v) {
        final AlertDialog load;
        AlertDialog.Builder loadBuilder = new AlertDialog.Builder(v.getContext());
        View loadView = getActivity().getLayoutInflater().inflate(R.layout.rep_more_info_loading, null);
        loadBuilder.setView(loadView);
        load = loadBuilder.create();

        final AlertDialog d;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        final View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.rep_more_info_popup, null);
        String[] date = end.split("-");
        String month = new DateFormatSymbols().getMonths()[Integer.parseInt(date[1]) - 1];

        builder.setView(inflatedView);
        d = builder.create();
        ImageView exit_button = (ImageView) inflatedView.findViewById(R.id.closePopUp);

        ImageView rep_image = (ImageView) inflatedView.findViewById(R.id.main_image);
        TextView rep_name = (TextView) inflatedView.findViewById(R.id.rep_name);
        TextView rep_party = (TextView) inflatedView.findViewById(R.id.rep_party);
        TextView end_term = (TextView) inflatedView.findViewById(R.id.end_term_date);
        final LinearLayout committees = (LinearLayout) inflatedView.findViewById(R.id.committees);
        final LinearLayout bills_sponsored = (LinearLayout) inflatedView.findViewById(R.id.bills_sponsored);

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        ImageView image = (ImageView) v.findViewById(R.id.rep_image);
        rep_image.setImageBitmap((Bitmap) image.getTag());

        CharSequence party = ((TextView) v.findViewById(R.id.rep_party)).getText();
        if (party.toString().equals("Democrat")) {
            rep_image.setBackgroundResource(R.drawable.outline_democrat);
        } else if (party.toString().equals("Republican")) {
            rep_image.setBackgroundResource(R.drawable.outline_republican);
        } else {
            rep_image.setBackgroundResource(R.drawable.outline_independent);
        }

        rep_name.setText(((TextView) v.findViewById(R.id.rep_name)).getText());
        rep_party.setText(party);
        end_term.setText(month + " " + date[2] + ", " + date[0]);

        String committeeHttpCall = sunlightUrl + "committees?member_ids=" + bioguide_id + "&apikey=" + sunlightApiKey;
        JsonObjectRequest commRequest = new JsonObjectRequest
                (Request.Method.GET, committeeHttpCall, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentCommittee = results.getJSONObject(i);
                                TextView newCommittee = new AutofitTextView(getContext());
                                newCommittee.setTextColor(getResources().getColor(R.color.more_info));
                                newCommittee.setTextSize(18);
                                newCommittee.setPadding(60, 10, 0 , 0);
                                newCommittee.setText("- " + currentCommittee.getString("name"));
                                committees.addView(newCommittee);
                            }

                        } catch (JSONException e) {
                            System.out.println(e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        String sponsoredHttpCall = sunlightUrl + "bills?sponsor_id=" + bioguide_id + "&apikey=" + sunlightApiKey;
        JsonObjectRequest sponsRequest = new JsonObjectRequest
                (Request.Method.GET, sponsoredHttpCall, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject currentBill = results.getJSONObject(i);
                                String short_title = currentBill.getString("short_title");
                                if (short_title.equals("null")) {
                                    continue;
                                }
                                TextView dateBill = new TextView(getContext());
                                TextView billName = new AutofitTextView(getContext());

                                dateBill.setTextColor(getResources().getColor(R.color.more_info));
                                dateBill.setTextSize(18);
                                dateBill.setPadding(60, 10, 0, 0);
                                String[] date = currentBill.getString("introduced_on").split("-");
                                String month = new DateFormatSymbols().getMonths()[Integer.parseInt(date[1]) - 1];
                                dateBill.setText("- " + month + " " + date[2] + ", " + date[0]);

                                billName.setTextColor(getResources().getColor(R.color.more_info));
                                billName.setTextSize(18);
                                billName.setPadding(90, 0, 0, 0);
                                billName.setText(currentBill.getString("short_title"));

                                bills_sponsored.addView(dateBill);
                                bills_sponsored.addView(billName);
                            }
                            load.dismiss();
                            d.show();
                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        MySingleton.getInstance(getContext()).addToRequestQueue(commRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(sponsRequest);
        load.show();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int position;
        String bioguide_id;
        View v;
        String term_end;

        public DownloadImageTask(ImageView bmImage, View v, int position, String bioguide_id, String term_end) {
            this.bmImage = bmImage;
            this.v = v;
            this.bioguide_id = bioguide_id;
            this.term_end = term_end;
            this.position = position;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap m = Bitmap.createBitmap(result, 0, 50, 450, 450);
            bmImage.setImageBitmap(m);
            bmImage.setTag(m);
            Integer col_number = getArguments().getInt("col_number");
            if (col_number != -1 && col_number == position) {
                inflateMoreInfo(bioguide_id, term_end, v);
            }
        }
    }

}
