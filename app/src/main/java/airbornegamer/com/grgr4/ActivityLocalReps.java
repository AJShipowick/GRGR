package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.CheckBox;
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
import java.util.Collections;
import java.util.Comparator;
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
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        dialog = ProgressDialog.show(context, "Finding Representatives", "Freedom loading...", true);
        repData = new LocalRepData(this);

        if (userHasSelectedStateManually(savedInstanceState)) {
            stateSpecificRepData = repData.buildStateSpecificData(stateAbbreviation);
            start_Main_UI_Flow();
        } else {
            buildPageBasedOnGPS();
        }
    }

    public boolean userHasSelectedStateManually(Bundle savedInstanceState) {
        String userSelectedState = getUserSelectedState(savedInstanceState);

        if (userSelectedState != null && userSelectedState.length() > 0) {
            String stateFullNameAndAbbreviation = repData.getStateAbbreviationAndFullName(userSelectedState);
            String[] StatePair = stateFullNameAndAbbreviation.split(",");
            stateFullName = StatePair[0];
            stateAbbreviation = StatePair[1];
            return true;
        }
        return false;
    }

    public void buildPageBasedOnGPS() {
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
            try {
                //Best case scenario to here.
                stateSpecificRepData = repData.buildStateSpecificData(stateAbbreviation);
                new selectRepsBasedOnZipCode().execute(zipCode);
            } catch (Exception ex) {
                //todo handle this
            }

        } else {
            Intent intent = new Intent(getApplicationContext(), ChangeState.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void start_Main_UI_Flow() {
        try {
            setupAdapter();
            new getRepInfoAndPictures().execute(stateSpecificRepData);
            dialog.dismiss();

        } catch (Exception ex) {
            //todo handle this
        }
    }

    LocalRepAdapter adapter = null;

    public void setupAdapter() {
        adapter = new LocalRepAdapter(this, R.layout.list_reps, new ArrayList<RepRow>());
        repsListView = (ListView) findViewById(R.id.listView_Reps);

        //Set the header including current state flag
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repData.buildCustomStateHeader(header, stateFullName);

        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setupChangeStateEventListeners();
    }

    public void addRepRowsToView(ArrayList<RepRow> repInfoAndPicture) {
        adapter = new LocalRepAdapter(this, R.layout.list_reps, repInfoAndPicture);
        repsListView = (ListView) findViewById(R.id.listView_Reps);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setupChangeStateEventListeners() {

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
        Collections.sort(stateSpecificRepData, new Comparator<RepDetailInfo>() {
            @Override
            public int compare(RepDetailInfo abc1, RepDetailInfo abc2) {
                return Boolean.compare(abc2.isUserRepresentative, abc1.isUserRepresentative);
            }
        });
    }

    private BitmapDrawable matchPictureToRepInfo(String repID) {

        AssetManager assets = getApplicationContext().getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open("repid" + repID + ".jpg")));

            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            if (bitmap == null) {
                return new BitmapDrawable(getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.unknownrep));
            }
            //GOOD!
            return new BitmapDrawable(getApplicationContext().getResources(), bitmap);

        } catch (Exception ex1) {
            //todo handle this
            try {
                return new BitmapDrawable(getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.unknownrep));
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

                BitmapDrawable repImage = matchPictureToRepInfo(repID);
                BitmapDrawable repPartyImage = findRepParty(repParty);
                String yourRepresentative = getMyRepresentativeText(params[0].get(i).isUserRepresentative, repTitle);
                Boolean repSelected = (yourRepresentative.equals(""))? false : true;

                RepRow newRepData = new RepRow(repImage, currentRep, repID, repPartyImage, yourRepresentative, repSelected);
                repRowToDisplay.add(newRepData);
            }
            return repRowToDisplay;
        }

        BitmapDrawable findRepParty(String repParty) {

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

    String getMyRepresentativeText(boolean isUserRepresentative, String repTitle) {

        if (isUserRepresentative) {
            return (repTitle.toUpperCase().equals("SEN") ? "Your Senator!" : "Your Representative!");
        } else {
            return "";
        }
    }

    //using Jsoup to parse HTML.
    //http://jsoup.org/apidocs/org/jsoup/nodes/Element.html#getElementsByClass-java.lang.String-
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
            start_Main_UI_Flow();
        }
    }

    public void buildCustomEmail(View view) {
        startActivity(new Intent(getApplicationContext(), BuildCustomEmailActivity.class));
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


