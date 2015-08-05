package airbornegamer.com.grgr4;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators

//https://www.opencongress.org/

public class ActivityLocalReps extends ActionBarActivity {

    private ImageView iv;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_local_reps);

        //Do all below stuff in long running async task;
        //http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask
        //http://developer.android.com/intl/ko/reference/android/os/AsyncTask.html

        String currentState = getCurrentUsersState();
        if (currentState != null && PhysicalStateIsKnown(currentState)){
            SetStateLabel(currentState);
            SetStateFlag(currentState);
            setRepresentativeInfo(currentState);
        }else{
            //State not know, do something (audit...write msg to user....for us user only at this time....Manually select state....)
        }
    }

    public String getCurrentUsersState(){

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        try{
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAdminArea();
        }catch(Exception ex){
            return null;
        }

    }

    public Boolean PhysicalStateIsKnown(String currentState){
        String[] knownStates = getResources().getStringArray(R.array.KnowStates);

        for(Integer i=0; i < knownStates.length; i++){
            String[] StatePair = knownStates[i].split(",");

            //StatePair[0] is full state name (Nebraska) ; StatePair[1] is state abbreviation (NE).
            if(StatePair[0].equals(currentState) || StatePair[1].equals(currentState)){
                return true;
            }
        }
        return false;
    }

    public void SetStateLabel(String currentState){
        TextView stateLabel = (TextView)findViewById(R.id.txtYourLocation);
        stateLabel.setText(currentState);
    }

    public void SetStateFlag(String currentState){
//                Find image of currentState and set below.....
//                ImageView stateImage = (ImageView)findViewById(R.id.imgYourState);
//                stateImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.flag_ne));
    }

    public void setRepresentativeInfo(String currentState){

        String repInfo = getRepresentativeInfo();

    }

    public String getRepresentativeInfo(){
        //https://www.govtrack.us/api/v2/role?state=ne&current=true
        //parse json
        // http://stackoverflow.com/questions/2845599/how-do-i-parse-json-from-a-java-httpresponse

        String repUrl = "https://www.govtrack.us/api/v2/role?state=ne&current=true";
        new DownloadLink().execute(repUrl);


       return null;
    }

    class DownloadLink extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL("https://www.govtrack.us/api/v2/role?state=ne&current=true");
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                StringBuilder sb = new StringBuilder();
                String line;
                //br = new BufferedReader(new InputStreamReader(new InputStreamReader(url.openStream()));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                JSONObject json = new JSONObject(sb.toString());
                //json.getString("status");
                //JSONArray jsonArray = new JSONArray(json.getString("object"));
                //JSONArray jsonArray = json.getJSONArray("object");


            }catch (Exception ex){
                String myEx = ex.toString();
            }


            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

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


