package com.github.connietruong.represent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Connie on 2/23/2016.
 */
public class ButtonsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.main_buttons_view, container, false);
        Typeface droidSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DroidSans.ttf");
        TextView orText = (TextView) v.findViewById(R.id.or);
        Button zipButton = (Button) v.findViewById(R.id.use_zip_code);
        Button locationButton = (Button) v.findViewById(R.id.use_location);
        orText.setTypeface(droidSans);
        zipButton.setTypeface(droidSans);
        locationButton.setTypeface(droidSans);
        return v;
    }
}
