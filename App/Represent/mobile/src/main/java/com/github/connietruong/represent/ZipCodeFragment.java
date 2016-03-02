package com.github.connietruong.represent;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Connie on 2/24/2016.
 */
public class ZipCodeFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.zip_code_view, container, false);
        Typeface droidSans = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DroidSans.ttf");
        EditText zipValue = (EditText) v.findViewById(R.id.zip_code);
        TextView invalidZip = (TextView) v.findViewById(R.id.invalid_zip);
        Button confirmButton = (Button) v.findViewById(R.id.find_reps);
        zipValue.setTypeface(droidSans);
        confirmButton.setTypeface(droidSans);
        invalidZip.setTypeface(droidSans);
        return v;
    }
}
