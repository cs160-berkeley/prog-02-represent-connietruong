package com.github.connietruong.represent;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Connie on 2/27/2016.
 */
public class RepresentativeDisplayFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Integer col_number = getArguments().getInt("col_number");
        final String zip_code = getArguments().getString("zip_code");
        final String location = getArguments().getString("location");

        View v = inflater.inflate(R.layout.representative_template_view, container, false);
        Typeface euphemia = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Euphemia.ttf");
        TextView rep_name = (TextView) v.findViewById(R.id.name);
        TextView rep_party = (TextView) v.findViewById(R.id.party);
        rep_name.setTypeface(euphemia);
        String repName = getArguments().getString("sen_or_rep") + " " + getArguments().getString("rep_name");
        rep_name.setText(repName);


        ImageView rep_image = (ImageView) v.findViewById(R.id.image);

        String party = getArguments().getString("rep_party");
        rep_party.setTypeface(euphemia);
        rep_party.setText(party);
        if (party.equalsIgnoreCase("Democrat")) {
            rep_party.setTextColor(Color.parseColor("#0075AC"));
            rep_image.setImageResource(R.drawable.democrat);
        } else if (party.equalsIgnoreCase("Republican")) {
            rep_party.setTextColor(Color.parseColor("#CB3B31"));
            rep_image.setImageResource(R.drawable.republican);
        } else {
            rep_party.setTextColor(Color.parseColor("#737373"));
            rep_image.setImageResource(R.drawable.independent);
        }

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                try {
                    Intent intent = new Intent(getActivity(), ElectionResultsDisplayActivity.class);
                    JSONObject service_info;
                    if (!zip_code.isEmpty()) {
                        service_info = new JSONObject(zip_code);
                    } else {
                        service_info = new JSONObject(location);
                    }
                    intent.putExtra("PERCENTAGES", service_info.getString("PERCENTAGES"));
                    intent.putExtra("COUNTY", service_info.getString("COUNTY"));
                    startActivity(intent);
                } catch (JSONException er) {

                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                Intent intent = new Intent(getActivity(), WatchToPhoneService.class);
                intent.putExtra("col_number", col_number.toString());
                JSONObject service_info;
                try {
                    if (!zip_code.isEmpty()) {
                        intent.putExtra("ZIP_CODE", zip_code);
                        service_info = new JSONObject(zip_code);
                    } else {
                        service_info = new JSONObject(location);
                        intent.putExtra("LOCATION", location);
                    }
                    if (!service_info.isNull("SHAKE_COUNTY")) {
                        intent.putExtra("SHAKE_COUNTY", service_info.getString("SHAKE_COUNTY"));
                    }

                    getActivity().startService(intent);
                    Toast toast = Toast.makeText(getActivity(), "More Info Sent!", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (JSONException e) {

                }
                return true;
            }

        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        return v;
    }

    public static RepresentativeDisplayFragment newInstance(String sen_or_rep, String rep_name, String rep_party,
                                                            String image_url, Integer col_number, String zip_code,
                                                            String location) {
        RepresentativeDisplayFragment instance = new RepresentativeDisplayFragment();
        Bundle b = new Bundle();
        b.putString("sen_or_rep", sen_or_rep);
        b.putString("rep_name", rep_name);
        b.putString("rep_party", rep_party);
        b.putString("image_url", image_url);
        b.putInt("col_number", col_number);
        b.putString("zip_code", zip_code);
        b.putString("location", location);

        instance.setArguments(b);
        return instance;
    }


}