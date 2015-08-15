package airbornegamer.com.grgr4;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class LocalRepData {

    Context mContext;

    public LocalRepData(Context mContext) {
        this.mContext = mContext;
    }

    public String getCurrentUsersState() {

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            String stateFullName = addresses.get(0).getAdminArea();

            String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
            for (int i = 0; i < knownStates.length; i++) {
                String[] StatePair = knownStates[i].split(",");
                //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
                if(stateFullName.equals(StatePair[0])){
                    return StatePair[1];
                }
            }
        } catch (Exception ex) {
            //bad
            return "UnknownState";
        }
        return "UnknownState";
    }

    public Boolean physicalStateIsKnown(String currentState) {

        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState)) {
                return true;
            }
        }
        return false;
    }

    //**View source control before 8/12/2015 for method to query data and use JSON results.
    public ArrayList<String> filterRepDataForUser(String currentState) {

        ArrayList<String> aList = new ArrayList<String>();

        String[] allRepData = mContext.getResources().getStringArray(R.array.RepData);
        for (int i = 0; i < allRepData.length; i++) {
            String[] RepArray = allRepData[i].split(",");
            if (currentState.equals(RepArray[1].substring(6))) {
                aList.add(RepArray[3].substring(6) + " " + RepArray[4].substring(10) + " " + RepArray[5].substring(9) + "(" + RepArray[0].substring(3) + ")");
            }
        }

        return aList;
    }
}


