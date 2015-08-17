package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//todo popular state facts? http://www.50states.com/
public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView repsListView;
    String stateFullName = "";
    String stateAbbreviation = "";

//    boolean userSelectedCustomState = false;
//    String userSelectedState = "";
//    public ActivityLocalReps(String state){
//        userSelectedCustomState = true;
//        userSelectedState = state;
//    }
//    public ActivityLocalReps(){
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        repData = new LocalRepData(this);

        String userSelectedState = getUserSelectedState(savedInstanceState);
        if (userSelectedState.length() > 0){
            String stateFullNameAndAbbreviation = repData.GetStateComboFromFullStateName(userSelectedState);
            String[] stateCombo = stateFullNameAndAbbreviation.split(",");
            stateFullName = stateCombo[0];
            buildRepresentativeData(stateCombo[1]);
        }else{
            InternetConnectivity internet = new InternetConnectivity();
            boolean userIsConnectedToInternet = internet.isConnected(getApplicationContext());

            String currentState = "";
            if (userIsConnectedToInternet){
                currentState = repData.getCurrentUsersState();
                String[] StatePair = currentState.split(",");
                stateFullName = StatePair[0];
                stateAbbreviation = StatePair[1];
            }else{
                currentState = "UnknownState";
            }

            if (!currentState.equals("UnknownState") && repData.physicalStateIsKnown(stateAbbreviation)) {
                buildRepresentativeData(stateAbbreviation);
            } else {
                //todo allow user to select their state and let them know we don't have a interent connection/their state is un-know (outside of the US?)
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }
    }

    private String getUserSelectedState(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                return "";
            } else {
                return extras.getString("StateName");
            }
        } else {
            return (String) savedInstanceState.getSerializable("StateName");
        }
    }

    //https://www.govtrack.us/developers/data
    //https://www.govtrack.us/data/photos/
    public void buildRepresentativeData(String currentState) {
        List<String> stateSpecificRepData = repData.filterRepDataForUser(currentState);
        ArrayList<Reps> repInfoAndPicture = CombineRepInfoAndPhoto(stateSpecificRepData);
        SetupAdapter(repInfoAndPicture);
    }

    public void SetupAdapter(ArrayList<Reps> repInfoAndPicture) {
        LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.list_reps, repInfoAndPicture);
        repsListView = (ListView) findViewById(R.id.listView_Reps);

        //Set the header including current state flag
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repData.BuildCustomStateHeader(header, stateFullName);

        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);

        setupChangeStateEventListener();
    }

    public void setupChangeStateEventListener() {
        TextView txtChangeStates = (TextView) findViewById(R.id.txtChangeState);
        txtChangeStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);

                //todo need newtask and finish?
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //finish();
            }
        });
    }

    public ArrayList<Reps> CombineRepInfoAndPhoto(List<String> stateSpecificRepData) {

        ArrayList<Reps> repsToDisplay = new ArrayList<Reps>();

        for (int i = 0; i < stateSpecificRepData.size(); i++) {
            String repID = stateSpecificRepData.get(i).substring(stateSpecificRepData.get(i).length() - 7, stateSpecificRepData.get(i).length()-1);
            String currentRep = stateSpecificRepData.get(i).substring(0, stateSpecificRepData.get(i).length() - 8);

            //Bitmap repImage = BitmapFactory.decodeResource(getResources(), R.drawable.repid300002);
            Bitmap repImage = MatchPictureToRepInfo(repID);

            Reps newRepData = new Reps(repImage, currentRep);
            repsToDisplay.add(newRepData);
        }
        return repsToDisplay;
    }

    private Bitmap MatchPictureToRepInfo(String repID) {
        int imgId = getResources().getIdentifier(getApplicationContext().getPackageName() + ":drawable/repid" + repID, null, null);
        Bitmap repBitmap = BitmapFactory.decodeResource(getResources(), imgId);

        if (repBitmap == null){
            return BitmapFactory.decodeResource(getResources(), R.drawable.unknown_representative);
        }
        return repBitmap;
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


