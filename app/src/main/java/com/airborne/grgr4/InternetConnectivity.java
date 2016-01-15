package com.airborne.grgr4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InternetConnectivity {

    Context mContext;
    CallBackListener mListener;

    public InternetConnectivity(Context context) {
        mContext = context;
    }

    public void setListener(CallBackListener listener) {
        mListener = listener;
    }

    public boolean isConnected() {
        try {

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() == null) {
                return false;
            }
            return cm.getActiveNetworkInfo().isConnected();
        } catch (Exception ex) {
            return false;
        }
    }

//    public Map<String, String> getUserLocation(LocalRepDataHelper repData) {
//        try {
//            new getUserLocationAsync().execute(repData).get();
//        } catch (Exception ex) {
//
//        }
//        return null;
//    }

     class getUserLocationAsync extends AsyncTask<LocalRepDataHelper, Void, Map<String, String>> {

        protected Map<String, String> doInBackground(LocalRepDataHelper... params) {

            Map<String, String> UserLocationInfo = new HashMap<>();

            try {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    UserLocationInfo.put("State", "UnknownState");
                    return UserLocationInfo;
                }

                LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location availableLocation = (gpsLocation != null) ? gpsLocation : networkLocation;

                if (availableLocation == null) {
                    UserLocationInfo.put("State", "UnknownState");
                    return UserLocationInfo;
                }

                double longitude = availableLocation.getLongitude();
                double latitude = availableLocation.getLatitude();

                Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.
                String zipCode = addresses.get(0).getPostalCode();
                String StateValues = params[0].getStateAbbreviationAndFullName(fullStateName);

                UserLocationInfo.put("State", StateValues);
                UserLocationInfo.put("ZipCode", zipCode);
                return UserLocationInfo;

            } catch (Exception ex) {
                //todo handle this
                UserLocationInfo.put("State", "UnknownState");
                return UserLocationInfo;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> userLocationInfo) {
            mListener.userLocationCallback(userLocationInfo);
        }
    }
}
