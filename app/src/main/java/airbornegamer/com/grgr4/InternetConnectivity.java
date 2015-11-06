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

    public Map<String, String> getCurrentUserLocation(Context mContext, LocalRepData repData) {

        Map<String, String> UserLocationInfo = new HashMap<>();
        UserLocationInfo.put("State", "UnknownState");

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Location locationNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null) {
            return UserLocationInfo;
        }

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.
            String zipCode = addresses.get(0).getPostalCode();

            //UserLocationInfo.clear();
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
