package com.github.connietruong.represent;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Connie on 2/25/2016.
 */
public class RepresentativesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Object[] feinstein = {"Senator", R.drawable.feinstein, "Dianne Feinstein", "Democrat",
                "\"Today we reflect on the contributions and leadership of U.S. presidents throughout our nation's great history. Happy #PresidentsDay.\"",
                "@SenFeinstein", "http://www.feinstein.senate.gov/public/", "https://www.feinstein.senate.gov/public/index.cfm/e-mail-me"};
        Object[] boxer = {"Senator", R.drawable.boxer, "Barbara Boxer", "Democrat",
                "\"Putting the country first means Obama nominating a Justice and the Senate doing its constitutional duty by voting on the nominee.\"",
                "@SenatorBoxer", "https://www.boxer.senate.gov/", "https://www.boxer.senate.gov/?p=shareyourviews"};
        Object[] lee = {"Representative", R.drawable.lee, "Barbara Lee", "Democrat", "\"I worked on this program as a young mom in #Oakland. Paved the way for today's school breakfasts.\"",
                "@RepBarbaraLee", "https://lee.house.gov/", "https://lee.house.gov/contact-the-office/email-me"};
        Object[][] repInfo = {feinstein, boxer, lee};
        //make api call here. have newInstance carry zip/location so we can make api call to get position's info

        View v = inflater.inflate(R.layout.representative_template_view, container, false);
        Typeface ebrima = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ebrima.ttf");
        TextView sen_or_rep = (TextView) v.findViewById(R.id.senator_or_rep);
        TextView rep_name = (TextView) v.findViewById(R.id.rep_name);
        TextView rep_party = (TextView) v.findViewById(R.id.rep_party);
        TextView tweet_text = (TextView) v.findViewById(R.id.tweet_text);
        TextView tweet_id = (TextView) v.findViewById(R.id.tweet_id);
        Button website_button = (Button) v.findViewById(R.id.website_button);
        Button email_button = (Button) v.findViewById(R.id.email_button);
        TextView more_info = (TextView) v.findViewById(R.id.more_info_button);
        ImageView rep_image = (ImageView) v.findViewById(R.id.rep_image);
        int position = getArguments().getInt("position");
        final Object[] representative = repInfo[position];

        sen_or_rep.setTypeface(ebrima);
        sen_or_rep.setText((String) representative[0]);

        rep_image.setImageResource((Integer) representative[1]);
        rep_image.setTag(representative[1]);
        //set background based on rep or dem

        rep_name.setTypeface(ebrima);
        rep_name.setText((String) representative[2]);

        rep_party.setTypeface(ebrima);
        rep_party.setText((String) representative[3]);

        tweet_text.setTypeface(ebrima);
        tweet_text.setText((String) representative[4]);

        tweet_id.setTypeface(ebrima);
        tweet_id.setText((String) representative[5]);

        website_button.setTypeface(ebrima);
        email_button.setTypeface(ebrima);
        more_info.setTypeface(ebrima, Typeface.BOLD);

        website_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) representative[6]));
                startActivity(browserIntent);
            }
        });
        //change button background based on rep or dem

        email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) representative[7]));
                startActivity(browserIntent);
            }
        });
        //change button background based on rep or dem

        more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog d;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.rep_more_info_popup, null);
                //EDIT VALUES HERE BEFORE SETTING THE VIEW

                builder.setView(inflatedView);
                d = builder.create();
                ImageView exit_button = (ImageView) inflatedView.findViewById(R.id.closePopUp);

                ImageView rep_image = (ImageView)inflatedView.findViewById(R.id.main_image);
                TextView rep_name = (TextView)inflatedView.findViewById(R.id.rep_name);
                TextView rep_party = (TextView)inflatedView.findViewById(R.id.rep_party);
                TextView end_term = (TextView) inflatedView.findViewById(R.id.end_term_date);
                LinearLayout committees = (LinearLayout) inflatedView.findViewById(R.id.committees);
                LinearLayout bills_sponsored = (LinearLayout) inflatedView.findViewById(R.id.bills_sponsored);

                exit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

                ImageView image = (ImageView) getView().findViewById(R.id.rep_image);
                rep_image.setImageResource((Integer)image.getTag());
                rep_name.setText(((TextView)getView().findViewById(R.id.rep_name)).getText());
                rep_party.setText(((TextView)getView().findViewById(R.id.rep_party)).getText());

                d.show();
            }
        });

        Integer col_number = getArguments().getInt("col_number");
        if (col_number != -1 && col_number == position) {
            final AlertDialog d;
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            final View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.rep_more_info_popup, null);
            //EDIT VALUES HERE BEFORE SETTING THE VIEW

            builder.setView(inflatedView);
            d = builder.create();
            ImageView exit_button = (ImageView) inflatedView.findViewById(R.id.closePopUp);

            ImageView pop_rep_image = (ImageView)inflatedView.findViewById(R.id.main_image);
            TextView pop_rep_name = (TextView)inflatedView.findViewById(R.id.rep_name);
            TextView pop_rep_party = (TextView)inflatedView.findViewById(R.id.rep_party);
            TextView end_term = (TextView) inflatedView.findViewById(R.id.end_term_date);
            LinearLayout committees = (LinearLayout) inflatedView.findViewById(R.id.committees);
            LinearLayout bills_sponsored = (LinearLayout) inflatedView.findViewById(R.id.bills_sponsored);

            exit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });

            pop_rep_image.setImageResource((Integer) representative[1]);
            pop_rep_name.setText(rep_name.getText());
            rep_party.setText(rep_party.getText());

            d.show();
        }

        return v;
    }

    public static RepresentativesFragment newInstance(int position, int col_number) {
        RepresentativesFragment instance = new RepresentativesFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        b.putInt("col_number", col_number);

        instance.setArguments(b);
        return instance;
    }

}
