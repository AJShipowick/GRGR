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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

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
        //http://developer.android.com/intl/ko/reference/android/os/AsyncTask.html

        String currentState = currentUserState();
        if (currentUserState() != null && PhysicalStateIsKnown(currentState)){
            setRepresentativePics(currentState);
        }


    }

    public String currentUserState(){

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
            if(knownStates[i].equals(currentState)){
                TextView stateLabel = (TextView)findViewById(R.id.txtYourLocation);
                stateLabel.setText(currentState);
                //also set an image of the state flag in top left banner area of page....
                return true;
            }
        }
        return false;
    }

    public void setRepresentativePics(String currentState){

        //https://www.govtrack.us/api/v2/role?state=ne&current=true
        //parse json
        // http://stackoverflow.com/questions/2845599/how-do-i-parse-json-from-a-java-httpresponse


        Integer stateReps = 0;

        //http://stackoverflow.com/questions/3090650/android-loading-an-image-from-the-web-with-asynctask



        TableLayout tblLocal = (TableLayout) findViewById(R.id.tblLocalReps);
        for (int i = 0; i < (stateReps / 3); i++) {

            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

            for (int j = 0; j < stateReps; j++) {

                ImageView view = new ImageView(this);
                view.setImageResource(R.drawable.mushroom);
                view.setId(j); //needs to match the image and rep name
                view.isClickable();
                tr.addView(view);
            }

            tblLocal.addView(tr);

        }
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
