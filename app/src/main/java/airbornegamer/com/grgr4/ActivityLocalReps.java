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
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView repsListView;
    //List<String> stateSpecificRepData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        InternetConnectivity internet = new InternetConnectivity();
        repData = new LocalRepData(this);

        boolean userIsConnectedToInternet = internet.isConnected(getApplicationContext());

        String currentState = "";
        if (userIsConnectedToInternet){
            currentState = repData.getCurrentUsersState();
        }else{
            currentState = "UnknownState";
        }

        if (!currentState.equals("UnknownState") && repData.physicalStateIsKnown(currentState)) {
            buildRepresentativeData(currentState);
        } else {
            //todo allow user to select their state and let them know we don't have a interent connection/their state is un-know (outside of the US?)
            buildRepresentativeData("NY");
        }
    }

    //https://www.govtrack.us/developers/data
    //https://www.govtrack.us/data/photos/
    public void buildRepresentativeData(String currentState) {
        List<String> stateSpecificRepData = repData.filterRepDataForUser(currentState);
        ArrayList<Reps> RepData = CombineRepInfoAndPhoto(stateSpecificRepData);
        SetupAdapter(RepData);
    }

    public void SetupAdapter(ArrayList<Reps> RepData) {
        LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.list_reps, RepData);
        repsListView = (ListView) findViewById(R.id.listView_Reps);
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);

        setupBtnToChangeStatesListener();
    }

    public void setupBtnToChangeStatesListener() {
        Button btnChangeStates = (Button) findViewById(R.id.btnChangeState);
        btnChangeStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);
                startActivity(intent);
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

    //public void setStateLabel(String currentState) {
    //TextView stateLabel = (TextView) findViewById(R.id.txtYourLocation);
    //stateLabel.setText(currentState);
    //}

    //public void setStateFlag(String currentState) {
//                Find image of currentState and set below.....
//                ImageView stateImage = (ImageView)findViewById(R.id.imgYourState);
//                stateImage.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.flag_ne));
    //}


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


