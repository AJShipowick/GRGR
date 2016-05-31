package com.airborne.grgr4;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentTheMovement extends Fragment {

    public FragmentTheMovement() {}

    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_themovement, container, false);
        CheckInternetPermissions();
        return view;
    }

        private void CheckInternetPermissions() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    //American Flag HEX Colors
    //http://www.colourlovers.com/palette/3263/American_Flag
    public static FragmentTheMovement newInstance(int sectionNumber) {
        FragmentTheMovement fragment = new FragmentTheMovement();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}

