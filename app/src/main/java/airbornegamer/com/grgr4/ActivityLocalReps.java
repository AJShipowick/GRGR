package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//todo popular state facts? http://www.50states.com/
public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView repsListView;
    String stateFullName = "";
    String stateAbbreviation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        repData = new LocalRepData(this);

        String userSelectedState = getUserSelectedState(savedInstanceState);
        if (userSelectedState != null && userSelectedState.length() > 0) {
            String stateFullNameAndAbbreviation = repData.GetStateComboFromFullStateName(userSelectedState);
            String[] stateCombo = stateFullNameAndAbbreviation.split(",");
            stateFullName = stateCombo[0];
            buildRepresentativeData(stateCombo[1]);
        } else {
            InternetConnectivity internet = new InternetConnectivity();
            boolean userIsConnectedToInternet = internet.isConnected(getApplicationContext());

            String currentState = "";
            if (userIsConnectedToInternet) {
                currentState = repData.getCurrentUsersState();
                String[] StatePair = currentState.split(",");
                stateFullName = StatePair[0];
                stateAbbreviation = StatePair[1];
            } else {
                currentState = "UnknownState";
            }

            if (!currentState.equals("UnknownState") && repData.physicalStateIsKnown(stateAbbreviation)) {
                buildRepresentativeData(stateAbbreviation);
            } else {
                //todo allow user to select their state and let them know we don't have a interent connection/their state is un-know (outside of the US?)
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private String getUserSelectedState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
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
        ArrayList<RepDetailInfo> stateSpecificRepData = repData.filterRepDataForUser(currentState);
        try {
            new getRepInfoAndPics().execute(stateSpecificRepData).get();
        } catch (Exception ex) {
            //todo handle this
        }
    }

    public void SetupAdapter(ArrayList<RepRow> repInfoAndPicture) {
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

                startActivity(intent);
            }
        });
        ImageView imgCurrentStates = (ImageView) findViewById(R.id.imgCurrentState);
        imgCurrentStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);

                startActivity(intent);
            }
        });
    }

    private BitmapDrawable MatchPictureToRepInfo(String repID) {

        AssetManager assets = getApplicationContext().getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open("repid" + repID + ".jpg")));

            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            if (bitmap == null) {
                InputStream unknownRepBuffer = new BufferedInputStream((assets.open("unknown_representative.png")));
                return new BitmapDrawable(getApplicationContext().getResources(), unknownRepBuffer);
            }
            return new BitmapDrawable(getApplicationContext().getResources(), bitmap);
        } catch (Exception ex1) {
            //todo handle this
            try {
                InputStream unknownRepBuffer = new BufferedInputStream((assets.open("unknown_representative.png")));
                return new BitmapDrawable(getApplicationContext().getResources(), unknownRepBuffer);
            } catch (Exception ex2) {
                //todo handle this
                return null;
            }
        }
    }

    private class getRepInfoAndPics extends AsyncTask<ArrayList<RepDetailInfo>, Void, ArrayList<RepRow>> {

        ArrayList<RepRow> repRowToDisplay = new ArrayList<>();

        protected ArrayList<RepRow> doInBackground(ArrayList<RepDetailInfo>... params) {

            String[] knownStates = getApplicationContext().getResources().getStringArray(R.array.KnowStates);
            for (int i = 0; i < params[0].size(); i++) {

                String repID = params[0].get(i).id;
//                String repState = params[0].get(i).state;
                String repParty = params[0].get(i).party;
                String repTitle = params[0].get(i).title;
                String repFirstName = params[0].get(i).firstName;
                String repLastName = params[0].get(i).lastName;

                repParty = repParty.substring(0, 1);
                repParty = "(" + repParty + ")";

                String currentRep = repTitle + " " + repFirstName + " " + repLastName + " " + repParty;

                BitmapDrawable repImage = MatchPictureToRepInfo(repID);
                BitmapDrawable repPartyImage = FindRepParty(repParty);

                RepRow newRepData = new RepRow(repImage, currentRep, repPartyImage);
                repRowToDisplay.add(newRepData);
            }
            return repRowToDisplay;
        }

        BitmapDrawable FindRepParty(String repParty) {

            AssetManager assets = getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer;
                if (repParty.contains("R")) {
                    buffer = new BufferedInputStream((assets.open("republican_elephant" + ".jpg")));
                } else {
                    buffer = new BufferedInputStream((assets.open("democratic_donkey" + ".jpg")));
                }

                Bitmap bitmap = BitmapFactory.decodeStream(buffer);
                return new BitmapDrawable(getApplicationContext().getResources(), bitmap);

            } catch (Exception ex) {
                //todo handle this
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<RepRow> repInfoAndPicture) {
            SetupAdapter(repRowToDisplay);
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


