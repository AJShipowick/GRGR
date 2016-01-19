package com.airborne.mobileminutemen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentYourRights extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FragmentYourRights newInstance(int sectionNumber) {
        FragmentYourRights fragment = new FragmentYourRights();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentYourRights() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yourrights, container, false);
    }
}
