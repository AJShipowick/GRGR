package airbornegamer.com.grgr4;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class LocalRepData {

    Context mContext;

    public LocalRepData(Context mContext) {
        this.mContext = mContext;
    }

    public String GetStateComboFromFullStateName(String fullStateName){
        String[] knownStates = mContext.getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if(fullStateName.equals(StatePair[0])){
                return StatePair[0] + "," + StatePair[1];
            }
        }
        return "UnknownState";
    }

    public String getCurrentUsersState() {

        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(mContext.getApplicationContext(), Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            String fullStateName = addresses.get(0).getAdminArea();//Gets state name from GPS.

            return GetStateComboFromFullStateName(fullStateName);

        } catch (Exception ex) {
            //bad
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

    public void BuildCustomStateHeader(View header, String stateFullName){
        //Set State Flag
        ImageView currentStateFlag = (ImageView) header.findViewById(R.id.imgCurrentState);
        currentStateFlag.setImageResource(findStateFlag(stateFullName));//R.drawable.new_mexico
        //Set State Outline
        ImageView currentStateOutline = (ImageView) header.findViewById(R.id.imgCurrentStateOutline);
        currentStateOutline.setImageResource((findStateOutline(stateFullName)));
        //Set State Name
        TextView txtCurrentState = (TextView) header.findViewById(R.id.txtCurrentState);
        txtCurrentState.setText(stateFullName);
    }

    public int findStateFlag(String stateFullName){
        String imageStateName = stateFullName.toLowerCase();
        if(imageStateName.contains(" ")){
            imageStateName = imageStateName.replace(" ", "_");
        }

        return mContext.getResources().getIdentifier(mContext.getApplicationContext().getPackageName() + ":drawable/" + imageStateName, null, null);

    }

    public int findStateOutline(String stateFullName){
        String imageStateName = stateFullName.toLowerCase();
        if(imageStateName.contains(" ")){
            imageStateName = imageStateName.replace(" ", "_");
        }

        imageStateName = imageStateName + "_outline";

        return mContext.getResources().getIdentifier(mContext.getApplicationContext().getPackageName() + ":drawable/" + imageStateName, null, null);
    }

}


