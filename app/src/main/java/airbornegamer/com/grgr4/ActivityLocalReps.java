package airbornegamer.com.grgr4;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators

//https://www.opencongress.org/


public class ActivityLocalReps extends ActionBarActivity {

    ListView localRepsListView;

    //ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_local_reps);

        //Do all below stuff in running async task;
        //http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask
        //http://developer.android.com/intl/ko/reference/android/os/AsyncTask.html

        localRepsListView = (ListView)findViewById(R.id.lstViewYourRepresentatives);
        ArrayList<String> myArrayList = new ArrayList<String>();

        String currentState = getCurrentUsersState();
        if (currentState != null && physicalStateIsKnown(currentState)) {
            setStateLabel(currentState);
            setStateFlag(currentState);
            getRepresentativeInfo(currentState);

            myArrayList = setRepresentativeInfoOntoScreen();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,myArrayList);
            localRepsListView.setAdapter(adapter);

        } else {
            //State not know, do something (audit...write msg to user....for us user only at this time....Manually select state....)
        }
    }

    public String getCurrentUsersState() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAdminArea();
        } catch (Exception ex) {
            return null;
        }

    }

    public Boolean physicalStateIsKnown(String currentState) {

        String[] knownStates = getResources().getStringArray(R.array.KnowStates);
        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if (StatePair[0].equals(currentState) || StatePair[1].equals(currentState)) {
                return true;
            }
        }
        return false;
    }

    public void setStateLabel(String currentState) {
        TextView stateLabel = (TextView) findViewById(R.id.txtYourLocation);
        stateLabel.setText(currentState);
    }

    public void setStateFlag(String currentState) {
//                Find image of currentState and set below.....
//                ImageView stateImage = (ImageView)findViewById(R.id.imgYourState);
//                stateImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.flag_ne));
    }

    public void getRepresentativeInfo(String currentState) {
        String repUrl = buildCustomAPIURL(currentState);
        new asyncPopulate_allRepData().execute(repUrl);
    }

    public String buildCustomAPIURL(String currentState){
        String firstPartOfURL = "https://www.govtrack.us/api/v2/role?state=";
        String lastPartOfURL = "&current=true";

        String[] knownStates = getResources().getStringArray(R.array.KnowStates);
        for(int i = 0; i < knownStates.length; i++){
            String[] StatePair = knownStates[i].split(",");
            if (currentState.equals(StatePair[0])){
                return firstPartOfURL + StatePair[1] + lastPartOfURL;
            }
        }
            return "";
    }

    JSONObject allRepData = new JSONObject();  //allRepData is populated with below call to a JSON object.
    class asyncPopulate_allRepData extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            InputStream inStream = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null) {
                    response += temp;
                }

                allRepData = (JSONObject) new JSONTokener(response).nextValue();

            } catch (Exception ex) {
                String myEx = ex.toString();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

    public ArrayList<String> setRepresentativeInfoOntoScreen(){
        try{
            JSONArray allRepsResults = allRepData.getJSONArray("objects");
            ArrayList<String> aList = new ArrayList<String>();

            for (int i = 0; i < allRepsResults.length(); i++) {
                JSONObject currentItem = allRepsResults.getJSONObject(i);
                JSONObject person = currentItem.getJSONObject("person");

                String firstName = person.getString("firstname");
                String lastName = person.getString("lastname");

                aList.add(firstName + " " + lastName);
            }
            return aList;
        }catch (Exception ex){
            //trace out bad stuff
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_local_reps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


