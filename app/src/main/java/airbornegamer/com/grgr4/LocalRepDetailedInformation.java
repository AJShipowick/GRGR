package airbornegamer.com.grgr4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LocalRepDetailedInformation extends AppCompatActivity {

    LocalRepDataHelper RepHelper;
    RepDetailInfo repDetailedInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_rep_detailed_information);

        RepHelper = new LocalRepDataHelper(this);
        RepHelper.localRepActivityHeader = false;

        Bundle extras = getIntent().getExtras();
        String repID = extras.getString("RepID");

        buildRepProfile(repID);

        String repParty = repDetailedInfo.party.substring(0, 1);
        repParty = "(" + repParty + ")";
        setTitle(repDetailedInfo.title + " " + repDetailedInfo.firstName + " " + repDetailedInfo.lastName + " " + repParty);
    }

    void buildRepProfile(String repID){


        ImageView repImage = (ImageView) findViewById(R.id.largeDetailedRepPic);
        repImage.setImageDrawable(RepHelper.matchPictureToRepInfo(repID));

        repDetailedInfo = RepHelper.buildSelectedRepInfo(repID);

        ImageView repStateView = (ImageView) findViewById(R.id.imgRepSelectedState);
        ImageView repStateOutlineView = (ImageView) findViewById(R.id.imgRepSelectedStateOutline);
        String fullStateName = RepHelper.getStateFullNameFromAbbreivation(repDetailedInfo.state);
        //repStateView.setImageDrawable(RepHelper.getCurrentStateFlagForDetailedRepInfo(fullStateName));

        //RepHelper.getFlag(fullStateName);
        RepHelper.getDetailedStateFlagAndOutline(fullStateName, repStateView, repStateOutlineView);

    }

//    public void callRep(View v) {
//
//        String phoneNumber = repDetailedInfo.phone;
//
//
//    }
//
//    public void emailRep(View v) {
//        String email = repDetailedInfo.email;
//
//
//    }
//
//    public void twitterRep(View v) {
//        String twitter = repDetailedInfo.twitter;
//
//
//    }
//
//    public void youTubeRep(View v) {
//        String youTube = repDetailedInfo.youTube;
//
//
//    }
//
//    public void websiteRep(View v) {
//        String website = repDetailedInfo.website;
//
//
//    }
}
