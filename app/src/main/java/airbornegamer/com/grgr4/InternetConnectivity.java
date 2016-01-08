package airbornegamer.com.grgr4;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InternetConnectivity {

    public boolean isConnected(Context appContext){
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(appContext.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null){return false;}
        return cm.getActiveNetworkInfo().isConnected();
    }

    public Map<String, String> getCurrentUserLocation(Context mContext, LocalRepDataHelper repData) {

        Map<String, String> UserLocationInfo = new HashMap<>();

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

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.
            String zipCode = addresses.get(0).getPostalCode();
            String StateValues = repData.getStateAbbreviationAndFullName(fullStateName);

            UserLocationInfo.put("State", StateValues);
            UserLocationInfo.put("ZipCode", zipCode);

            return UserLocationInfo;

        } catch (Exception ex) {
            //todo handle this
            return UserLocationInfo;
        }
    }
}
