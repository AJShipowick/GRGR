package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.content.Context;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

//todo popular state facts? http://www.50states.com/
public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView repsListView;
    String stateFullName = "";
    String stateAbbreviation = "";
    String zipCode = "";
    ArrayList<RepDetailInfo> stateSpecificRepData;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        repData = new LocalRepData(this);

        //User has selected the state manually
        String userSelectedState = getUserSelectedState(savedInstanceState);
        if (userSelectedState != null && userSelectedState.length() > 0) {
            String stateFullNameAndAbbreviation = repData.getStateAbbreviationAndFullName(userSelectedState);
            String[] StatePair = stateFullNameAndAbbreviation.split(",");
            stateFullName = StatePair[0];
            stateAbbreviation = StatePair[1];

            start_Main_UI_Flow();

        //Program selects state based off GPS
        } else {
            InternetConnectivity internet = new InternetConnectivity();
            boolean userIsConnectedToInternet = internet.isConnected(getApplicationContext());
            String currentState;
            if (userIsConnectedToInternet) {

                Map<String, String> StateData = internet.getCurrentUserLocation(this, repData);
                currentState = StateData.get("State");
                zipCode = StateData.get("ZipCode");

                if (!currentState.equals("UnknownState")) {
                    String[] StatePair = currentState.split(",");
                    stateFullName = StatePair[0];
                    stateAbbreviation = StatePair[1];
                }
            } else {
                currentState = "UnknownState";
            }

            if (!currentState.equals("UnknownState") && repData.stateIsKnown(stateAbbreviation)) {

                start_Main_UI_Flow();

                try{
                    new selectRepsBasedOnZipCode().execute(zipCode);
                }catch (Exception ex){
                    //todo handle this
                }

            } else {
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }

    public void start_Main_UI_Flow(){
        try{
            stateSpecificRepData = repData.buildStateSpecificData(stateAbbreviation);
            setupAdapter();
            new getRepInfoAndPictures().execute(stateSpecificRepData);

        }catch (Exception ex){
           //todo handle this
        }
    }

    LocalRepAdapter adapter = null;
    public void setupAdapter() {
        //ArrayList<RepRow> blankRepRows = new  ArrayList<>();
        adapter = new LocalRepAdapter(this, R.layout.list_reps, new ArrayList<RepRow>());
        repsListView = (ListView) findViewById(R.id.listView_Reps);

        //Set the header including current state flag
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repData.buildCustomStateHeader(header, stateFullName);

        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setupChangeStateEventListener();
    }

    public void addRepRowsToView(ArrayList<RepRow> repInfoAndPicture){
        //ListView currentRepListView = (ListView) findViewById(R.id.listView_Reps);

        adapter = new LocalRepAdapter(this, R.layout.list_reps, repInfoAndPicture);

        repsListView = (ListView) findViewById(R.id.listView_Reps);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    public void setRepAsLocalRep(ArrayList<String> specificReps) {

        //Compares all reps in state reps for user's specific zip code.
        for (RepDetailInfo stateRepresentative : stateSpecificRepData) {

            String stateRepFullName = stateRepresentative.firstName + " " + stateRepresentative.lastName;
            for (String localRepFullName : specificReps) {
                if (localRepFullName.equals(stateRepFullName) || (localRepFullName.contains(stateRepresentative.lastName))) {
                    stateRepresentative.isUserRepresentative = true;
                }
            }
        }

        //now do something with stateSpecificRepData to show user that this rep is their rep...also move these reps to the top of the list.....

    }

    private BitmapDrawable MatchPictureToRepInfo(String repID) {

        AssetManager assets = getApplicationContext().getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open("repid" + repID + ".jpg")));

            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            if (bitmap == null) {
                return new BitmapDrawable( getResources(), BitmapFactory.decodeResource(context.getResources() , R.drawable.unknownrep));
            }
            //GOOD!
            return new BitmapDrawable(getApplicationContext().getResources(), bitmap);

        } catch (Exception ex1) {
            //todo handle this
            try {
                return new BitmapDrawable( getResources(), BitmapFactory.decodeResource(context.getResources() , R.drawable.unknownrep));
            } catch (Exception ex2) {
                //todo handle this
                return null;
            }
        }
    }

    private class getRepInfoAndPictures extends AsyncTask<ArrayList<RepDetailInfo>, Void, ArrayList<RepRow>> {

        ArrayList<RepRow> repRowToDisplay = new ArrayList<>();

        protected ArrayList<RepRow> doInBackground(ArrayList<RepDetailInfo>... params) {

            for (int i = 0; i < params[0].size(); i++) {

                String repID = params[0].get(i).id;
                //String repState = params[0].get(i).state;
                String repParty = params[0].get(i).party;
                String repTitle = params[0].get(i).title;
                String repFirstName = params[0].get(i).firstName;
                String repLastName = params[0].get(i).lastName;

                repParty = repParty.substring(0, 1);
                repParty = "(" + repParty + ")";

                String currentRep = repTitle + " " + repFirstName + " " + repLastName + " " + repParty;

                BitmapDrawable repImage = MatchPictureToRepInfo(repID);
                BitmapDrawable repPartyImage = FindRepParty(repParty);

                RepRow newRepData = new RepRow(repImage, currentRep, repID, repPartyImage);
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
            addRepRowsToView(repInfoAndPicture);
        }
    }

    //using Jsoup to parse HTML.
    //http://jsoup.org/apidocs/org/jsoup/nodes/Element.html#getElementsByClass-java.lang.String-

    //todo - use landing page : https://www.opencongress.org/search/result?q=68512&search_people=1
    //http://www.41post.com/4650/programming/android-coding-a-loading-screen-part-3
    //or figure out a better way to async this shit.

    private class selectRepsBasedOnZipCode extends AsyncTask<String, Void, ArrayList<String>> {

        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> UserRepsBasedOnZip = new ArrayList<>();

            String firstPartOfURL = "https://www.opencongress.org/search/result?q=";
            String lastPartOfURL = "&search_people=1";
            String urlToParse = firstPartOfURL + params[0] + lastPartOfURL;

            try {
                Document doc = Jsoup.connect(urlToParse).get();
                Elements content = doc.getElementsByClass("name");

                for (Element name : content) {
                    UserRepsBasedOnZip.add(name.childNode(0).toString().trim());
                }

            } catch (Exception ex) {
                //todo handle ex here
            }

            return UserRepsBasedOnZip;
        }

        @Override
        protected void onPostExecute(ArrayList<String> UserRepsBasedOnZip) {
            setRepAsLocalRep(UserRepsBasedOnZip);
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


