package com.airborne.grgr4;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class LocalRepDataHelper {

    Context mContext;

    public LocalRepDataHelper(Context mContext) {
        this.mContext = mContext;
    }

    BitmapDrawable matchPictureToRepInfo(String repID) {

        AssetManager assets = mContext.getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open("repid" + repID + ".jpg")));

            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            if (bitmap == null) {
                return new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknownrep));
            }
            //GOOD!
            return new BitmapDrawable(mContext.getResources(), bitmap);

        } catch (Exception ex1) {
            //todo handle this
            try {
                return new BitmapDrawable(mContext.getResources(), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.unknownrep));
            } catch (Exception ex2) {
                //todo handle this
                return null;
            }
        }
    }

    public String getStateAbbreviationAndFullName(String fullStateName) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (fullStateName.equals(StatePair[0]))
                return StatePair[0] + "," + StatePair[1];
        }
        return "UnknownState";
    }

    public Boolean stateIsKnown(String currentState) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (nebraska_outline).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState))
                return true;
        }
        return false;
    }

    public ArrayList<RepDetailInfo> buildStateSpecificData(String currentState) {

        ArrayList<RepDetailInfo> allRepInfo = new ArrayList<>();

        String[] allRepData = mContext.getResources().getStringArray(R.array.repData);
        for (int i = 0; i < allRepData.length; i++) {
            String[] RepArray = allRepData[i].split(",");
            String state = RepArray[1].substring(6);
            if (currentState.equals(state)) {

                String repID = RepArray[0].substring(3);
                String repParty = RepArray[2].substring(6);
                String repTitle = RepArray[3].substring(6);
                String repFirstName = RepArray[4].substring(10);
                String repLastName = RepArray[5].substring(9);
                String repAddress = RepArray[6].substring(8);
                String repPhone = RepArray[7].substring(6);
                String repWebsite = RepArray[8].substring(8);
                String repTwitter = RepArray[9].substring(8);
                String repYouTube = RepArray[10].substring(8);
                String repEmail = RepArray[11].substring(13);

                RepDetailInfo currentRepInfo = new RepDetailInfo(repID, state, repParty, repTitle, repFirstName, repLastName, false, repAddress, repPhone, repWebsite, repTwitter, repYouTube, repEmail);

                allRepInfo.add(currentRepInfo);
            }
        }
        Collections.sort(allRepInfo, new sortStateSpecificData());
        return allRepInfo;
    }

    //Sorts list of representatives.
    public class sortStateSpecificData implements Comparator<RepDetailInfo> {
        @Override
        public int compare(RepDetailInfo o1, RepDetailInfo o2) {
            return o1.lastName.compareTo(o2.lastName);
        }
    }

    View myHeader;

    public void buildCustomStateHeader(View header, String stateFullName) {

        //Set State Flag
        myHeader = header;
        try {
           new getCurrentStateFlagForHeader().execute(stateFullName).get();
        } catch (Exception ex) {
            //todo handle this
        }

        //Set State Outline
        try {
            new getCurrentStateOutline().execute(stateFullName).get();
        } catch (Exception ex) {
            //todo handle this
        }

        //Set State Name
        TextView txtCurrentState = (TextView) header.findViewById(R.id.txtCurrentState);
        txtCurrentState.setText(stateFullName);
    }

    private class getCurrentStateFlagForHeader extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {

            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" "))
                imageStateName = imageStateName.replace(" ", "_");

            AssetManager assets = mContext.getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".jpg")));
                Bitmap bitmap = BitmapFactory.decodeStream(buffer);
                BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);
                return bmDrawable.getBitmap();
            } catch (Exception ex) {
                //todo handle this
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap currentFlag) {
            asyncCallbackSetStateFlag(currentFlag);
        }
    }

    public void asyncCallbackSetStateFlag(Bitmap stateFlag) {
        ImageView currentStateFlag = (ImageView) myHeader.findViewById(R.id.imgCurrentState);
        currentStateFlag.setImageBitmap(stateFlag);
    }

    public class getCurrentStateOutline extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" ")) {
                imageStateName = imageStateName.replace(" ", "_");
            }

            imageStateName = imageStateName + "_outline";

            AssetManager assets = mContext.getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".png")));
                Bitmap bitmap = BitmapFactory.decodeStream(buffer);
                BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);
                return bmDrawable.getBitmap();
            } catch (Exception ex) {
                //todo handle this
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap currentFlag) {
            asyncCallbackSetStateOutline(currentFlag);
        }
    }

    public void asyncCallbackSetStateOutline(Bitmap stateOutline) {
        ImageView currentStateOutline = (ImageView) myHeader.findViewById(R.id.imgCurrentStateOutline);
        currentStateOutline.setImageBitmap(stateOutline);
    }
}



