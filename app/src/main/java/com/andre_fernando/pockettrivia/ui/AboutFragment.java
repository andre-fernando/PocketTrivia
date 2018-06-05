package com.andre_fernando.pockettrivia.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.andre_fernando.pockettrivia.R;

/**
 * Gives basic info about app.
 */
public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ImageButton ib_github = view.findViewById(R.id.ib_github_logo);
        ImageButton ib_opentdb = view.findViewById(R.id.ib_opentdb_logo);

        ib_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/andre-fernando")); //NON-NLS
                startActivity(intent);
            }
        });

        ib_opentdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://opentdb.com/")); //NON-NLS
                startActivity(intent);
            }
        });

        return view;
    }

}
