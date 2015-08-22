package airbornegamer.com.grgr4;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class LocalRepData {

    Context mContext;
    public LocalRepData(Context mContext) {
        this.mContext = mContext;
    }

    public String GetStateComboFromFullStateName(String fullStateName) {
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (fullStateName.equals(StatePair[0])) {
                return StatePair[0] + "," + StatePair[1];
            }
        }
        return "UnknownState";
    }

    public String getCurrentUsersState() {

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location locationNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null){return "UnknownState";}

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.

            return GetStateComboFromFullStateName(fullStateName);

        } catch (Exception ex) {
            //todo handle this
            return "UnknownState";
        }
    }

    public Boolean physicalStateIsKnown(String currentState) {

        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (nebraska_outline).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState)) {
                return true;
            }
        }
        return false;
    }

    //**View source control before 8/12/2015 for method to query data and use JSON results.
    public ArrayList<RepDetailInfo> filterRepDataForUser(String currentState) {

        ArrayList<RepDetailInfo> allRepInfo = new ArrayList<>();

        String[] allRepData = mContext.getResources().getStringArray(R.array.RepData);
        for (int i = 0; i < allRepData.length; i++) {
            String[] RepArray = allRepData[i].split(",");
            String state = RepArray[1].substring(6);
            if (currentState.equals(state)) {

                String repID = RepArray[0].substring(3);
                String repState = RepArray[1].substring(6);
                String repParty = RepArray[2].substring(6);
                String repTitle = RepArray[3].substring(6);
                String repFirstName = RepArray[4].substring(10);
                String repLastName = RepArray[5].substring(9);

                //todo sort here?

                RepDetailInfo currentRepInfo = new RepDetailInfo(repID, repState, repParty, repTitle, repFirstName, repLastName);
                allRepInfo.add(currentRepInfo);
            }
        }

        return allRepInfo;
    }

    View myHeader;
    public void BuildCustomStateHeader(View header, String stateFullName) {
        //Set State Flag
        myHeader = header;
        try {
            new getCurrentStateFlag().execute(stateFullName).get();
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


    public void AsyncCallbackSetStateFlag(Bitmap stateFlag) {
        ImageView currentStateFlag = (ImageView) myHeader.findViewById(R.id.imgCurrentState);
        currentStateFlag.setImageBitmap(stateFlag);
    }


    public void AsyncCallbackSetStateOutline(Bitmap stateOutline) {
        ImageView currentStateOutline = (ImageView) myHeader.findViewById(R.id.imgCurrentStateOutline);
        currentStateOutline.setImageBitmap(stateOutline);
    }

    public Bitmap findStateFlag(String stateFullName) {
        String imageStateName = stateFullName.toLowerCase();
        if (imageStateName.contains(" ")) {
            imageStateName = imageStateName.replace(" ", "_");
        }

        AssetManager assets = mContext.getApplicationContext().getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".jpg")));
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            BitmapDrawable bmDrawable = new BitmapDrawable(mContext.getApplicationContext().getResources(), bitmap);
            return bmDrawable.getBitmap();
        } catch (Exception ex) {
            Toast.makeText(mContext.getApplicationContext(), "Error getting state flag :(", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public Bitmap findStateOutline(String stateFullName) {
        String imageStateName = stateFullName.toLowerCase();
        if (imageStateName.contains(" ")) {
            imageStateName = imageStateName.replace(" ", "_");
        }

        imageStateName = imageStateName + "_outline";

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

    private class getCurrentStateFlag extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {

            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" ")) {
                imageStateName = imageStateName.replace(" ", "_");
            }

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
            AsyncCallbackSetStateFlag(currentFlag);
        }
    }

    private class getCurrentStateOutline extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            String imageStateName = params[0].toLowerCase();
            if (imageStateName.contains(" ")) {
                imageStateName = imageStateName.replace(" ", "_");
            }

            imageStateName = imageStateName + "_outline";

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
            AsyncCallbackSetStateOutline(currentFlag);
        }
    }
}


