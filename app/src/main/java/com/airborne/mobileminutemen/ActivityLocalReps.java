package com.airborne.mobileminutemen;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class ActivityLocalReps extends Activity implements CallBackListener {

    LocalRepDataHelper repDataHelper = null;
    ListView repsListView;
    String stateFullName = "";
    String stateAbbreviation = "";
    String zipCode = "";
    String currentState = "";
    ArrayList<RepDetailInfo> stateSpecificRepData;
    Context context = this;
    ProgressDialog dialog;
    InternetConnectivity internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        dialog = ProgressDialog.show(context, "Looking up State Data", "Freedom loading...", true);
        repDataHelper = new LocalRepDataHelper(this);

        if (userHasSelectedStateManually(savedInstanceState)) {
            stateSpecificRepData = repDataHelper.buildStateSpecificData(stateAbbreviation);
            start_Main_UI_Flow();
        } else {
            buildPageBasedOnGPS();
        }
    }

    public boolean userHasSelectedStateManually(Bundle savedInstanceState) {
        String userSelectedState = getUserSelectedState(savedInstanceState);

        if (userSelectedState != null && userSelectedState.length() > 0) {
            String stateFullNameAndAbbreviation = repDataHelper.getStateAbbreviationAndFullName(userSelectedState);
            String[] StatePair = stateFullNameAndAbbreviation.split(",");
            stateFullName = StatePair[0];
            stateAbbreviation = StatePair[1];
            return true;
        }
        return false;
    }

    public void buildPageBasedOnGPS() {
        internet = new InternetConnectivity(context);
        internet.setListener(this);

        if (internet.isConnected()) {
            internet.new getUserLocationAsync().execute(repDataHelper);
        } else {
            currentState = "UnknownState";
            finishBuildingLocalRepPage();
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

    public void userLocationCallback(Map<String, String> userLocationInfo) {
        currentState = userLocationInfo.get("State");
        zipCode = userLocationInfo.get("ZipCode");

        if (!currentState.equals("UnknownState")) {
            String[] StatePair = currentState.split(",");
            stateFullName = StatePair[0];
            stateAbbreviation = StatePair[1];
        }
        finishBuildingLocalRepPage();
    }

    public void finishBuildingLocalRepPage(){
        if (!currentState.equals("UnknownState") && repDataHelper.stateIsKnown(stateAbbreviation)) {
            try {
                //Best case scenario to here.
                stateSpecificRepData = repDataHelper.buildStateSpecificData(stateAbbreviation);
                new selectRepsBasedOnZipCode().execute(zipCode);
            } catch (Exception ex) {
                //todo handle this
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), ChangeState.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            //display error with internet connection to user.
            if(internet != null && internet.mInternetConnectionStatus != null && !internet.mInternetConnectionStatus.equals("")){
                Toast.makeText(context, internet.mInternetConnectionStatus, Toast.LENGTH_LONG).show();
            }
        }
    }

    LocalRepAdapter adapter = null;
    public void setupAdapter() {
        adapter = new LocalRepAdapter(this, R.layout.list_reps, new ArrayList<RepRow>());
        repsListView = (ListView) findViewById(R.id.listView_Reps);

        //Set the header including current state flag
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repDataHelper.buildCustomStateHeader(header, stateFullName);

        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        setupChangeStateEventListeners();
    }

    String phoneNumber;
    String email;
    String twitter;
    String youTube;
    String website;
    String address;
    public void addRepRowsToView(final ArrayList<RepRow> repInfoAndPicture) {
        adapter = new LocalRepAdapter(this, R.layout.list_reps, repInfoAndPicture);
        repsListView = (ListView) findViewById(R.id.listView_Reps);
        repsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        repsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                RepRow currentRep = (RepRow) adapterView.getAdapter().getItem(position);

                if (currentRep == null) {
                    return;
                }

                LayoutInflater factory = LayoutInflater.from(context);

                final View msgBoxView = factory.inflate(R.layout.detailed_rep_popup_info, null);

                for (RepDetailInfo stateRepresentative : stateSpecificRepData) {
                    if (stateRepresentative.id.equals(currentRep.repID)) {
                        phoneNumber = stateRepresentative.phone;
                        email = stateRepresentative.email;
                        twitter = stateRepresentative.twitter;
                        youTube = stateRepresentative.youTube;
                        website = stateRepresentative.website;
                        address = stateRepresentative.address;
                        break;
                    }
                }

                final AlertDialog.Builder alertAdd = new AlertDialog.Builder(context);
                final AlertDialog alert = alertAdd.create();

                ImageView currentRepImage = (ImageView) msgBoxView.findViewById(R.id.currentRepPopupImage);
                currentRepImage.setImageDrawable(currentRep.repPic);

                removeUnknownContactMethods(msgBoxView);

                alertAdd.setView(msgBoxView);
                alertAdd.setTitle(currentRep.repInfo);
                //alertAdd.setMessage("Contact your representative today!");
                alertAdd.setNeutralButton("done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alert.cancel();
                    }
                });
                alertAdd.show();
            }
        });
    }

    public void removeUnknownContactMethods(View msgBoxView) {

        if (email.equals("null") || email.equals("")) {
            ImageView imgEmail = (ImageView) msgBoxView.findViewById(R.id.imgEmail);
            imgEmail.setVisibility(View.GONE);
        }
        if (twitter.equals("null") || twitter.equals("")) {
            ImageView imgTwitter = (ImageView) msgBoxView.findViewById(R.id.imgTwitter);
            imgTwitter.setVisibility(View.GONE);
        }
        if (website.equals("null") || website.equals("")) {
            ImageView imgWebsite = (ImageView) msgBoxView.findViewById(R.id.imgWebsite);
            imgWebsite.setVisibility(View.GONE);
        }
        if (youTube.equals("null") || youTube.equals("")) {
            ImageView imgYouTube = (ImageView) msgBoxView.findViewById(R.id.imgYouTube);
            imgYouTube.setVisibility(View.GONE);
        }
    }

    public void callRep(View v) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    public void emailRep(View v) {
        InternetConnectivity internet = new InternetConnectivity(context);
        if (!internet.isConnected()) { return; }

        Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(email));
        startActivity(emailIntent);
    }

    public void twitterRep(View v) {
        InternetConnectivity internet = new InternetConnectivity(context);
        if (!internet.isConnected()) { return; }

        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
        startActivity(twitterIntent);
    }

    public void youTubeRep(View v) {
        InternetConnectivity internet = new InternetConnectivity(context);
        if (!internet.isConnected()) { return; }

        Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youTube));
        startActivity(youTubeIntent);
    }

    public void websiteRep(View v) {
        InternetConnectivity internet = new InternetConnectivity(context);
        if (!internet.isConnected()) { return; }

        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        startActivity(websiteIntent);
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

    private class getRepInfoAndPictures extends AsyncTask<ArrayList<RepDetailInfo>, Void, ArrayList<RepRow>> {

        ArrayList<RepRow> repRowToDisplay = new ArrayList<>();

        protected ArrayList<RepRow> doInBackground(ArrayList<RepDetailInfo>... params) {

            for (int i = 0; i < params[0].size(); i++) {

                String repID = params[0].get(i).id;
                //String repStateView = params[0].get(i).state;
                String repParty = params[0].get(i).party;
                String repTitle = params[0].get(i).title;
                String repFirstName = params[0].get(i).firstName;
                String repLastName = params[0].get(i).lastName;

                repParty = repParty.substring(0, 1);
                repParty = "(" + repParty + ")";

                String currentRep = repTitle + " " + repFirstName + " " + repLastName + " " + repParty;

                BitmapDrawable repImage = repDataHelper.matchPictureToRepInfo(repID);
                BitmapDrawable repPartyImage = findRepParty(repParty);
                String yourRepresentative = getMyRepresentativeText(params[0].get(i).isUserRepresentative, repTitle);

                RepRow newRepData = new RepRow(repImage, currentRep, repID, repPartyImage, yourRepresentative);
                repRowToDisplay.add(newRepData);
            }
            return repRowToDisplay;
        }

        BitmapDrawable findRepParty(String repParty) {

            AssetManager assets = getApplicationContext().getResources().getAssets();
            try {
                InputStream buffer;
                if (repParty.equals("(R)")) {
                    buffer = new BufferedInputStream((assets.open("republican_elephant.jpg")));
                } else if (repParty.equals("(D)")) {
                    buffer = new BufferedInputStream((assets.open("democratic_donkey.jpg")));
                } else {
                    buffer = new BufferedInputStream((assets.open("unknown_representative.png")));
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


