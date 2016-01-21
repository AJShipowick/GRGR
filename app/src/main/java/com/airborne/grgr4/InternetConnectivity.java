package com.airborne.grgr4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InternetConnectivity {

    Context mContext;
    CallBackListener mListener;
    String mInternetConnectionStatus;

    public InternetConnectivity(Context context) {
        mContext = context;
    }

    public void setListener(CallBackListener listener) {
        mListener = listener;
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (cm.getActiveNetworkInfo() == null || !networkInfo.isConnected()) {
                Toast.makeText(mContext, R.string.internet_not_connected, Toast.LENGTH_LONG).show();
                return false;
            }

            //GOOD, we have a solid internet connection!
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    //todo query without geocoder every time: http://stackoverflow.com/questions/15182853/android-geocoder-getfromlocationname-always-returns-null
    class getUserLocationAsync extends AsyncTask<String, Void, Map<String, String>> {

        protected Map<String, String> doInBackground(String... params) {

            Map<String, String> UserLocationInfo = new HashMap<>();
            UserLocationInfo.put("State", "UnknownState");
            mInternetConnectionStatus = "";

            try {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    mInternetConnectionStatus = mContext.getString(R.string.internet_permissions_disabled);
                    return UserLocationInfo;
                }

                LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location availableLocation = (gpsLocation != null) ? gpsLocation : networkLocation;

                if (availableLocation == null) {
                    mInternetConnectionStatus = mContext.getString(R.string.internet_no_gps_location);
                    return UserLocationInfo;
                }

                double longitude = availableLocation.getLongitude();
                double latitude = availableLocation.getLatitude();

                Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses == null  || addresses.size() == 0){ return UserLocationInfo; } //bad

                String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.
                String zipCode = addresses.get(0).getPostalCode();

                LocalRepDataHelper repDataHelper = new LocalRepDataHelper(mContext);
                String StateValues = repDataHelper.getStateAbbreviationAndFullName(fullStateName);

                UserLocationInfo.clear();
                UserLocationInfo.put("State", StateValues);
                UserLocationInfo.put("ZipCode", zipCode);
                return UserLocationInfo;

            } catch (Exception ex) {
                mInternetConnectionStatus = mContext.getString(R.string.internet_gps_failed);
                return UserLocationInfo;
            }
        }

        @Override
        protected void onPostExecute(Map<String, String> userLocationInfo) {
            mListener.userLocationCallback(userLocationInfo);
        }
    }
}
