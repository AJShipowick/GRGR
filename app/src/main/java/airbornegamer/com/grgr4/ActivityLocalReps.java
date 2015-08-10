package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView repsListView;
    List<String> stateSpecificRepData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        //https://github.com/nostra13/Android-Universal-Image-Loader/wiki
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);

        repData = new LocalRepData(this);
        String currentState = repData.getCurrentUsersState();

        if (currentState != null && repData.physicalStateIsKnown(currentState)) {
            String repURL = repData.buildCustomDataAPIURL(currentState);
            AsyncTask queryReps = new QueryRepData(this, repURL).execute();
        } else {
            //State not know, do something (audit...write msg to user....for us user only at this time....Manually select state....)
        }
    }

    //Callback method after AsyncTask queryReps completes!
    public void filterRepData(JSONObject allRepData) {
        stateSpecificRepData = repData.filterRepDataForUser(allRepData);

        //build name and photo Reps list of classes
        //https://www.govtrack.us/developers/data
        //https://www.govtrack.us/data/photos/

        LocalRepAdapter adapter = SetupAdapter();
        CombineRepInfoAndPhoto(repData, stateSpecificRepData, adapter);
        DisplayData();
    }

    ArrayList<Reps> RepData = new ArrayList<Reps>();
    public LocalRepAdapter SetupAdapter(){
        LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.mylist, RepData);
        repsListView = (ListView) findViewById(R.id.listView);
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);

        return adapter;
    }

    public void CombineRepInfoAndPhoto(LocalRepData repData, List<String> stateSpecificRepData, LocalRepAdapter adapter) {

        for (int i = 0; i < stateSpecificRepData.size(); i++) {
            String currentRep = stateSpecificRepData.get(i);
            int currentRepIdStartIndex = currentRep.indexOf("(");

            String currentRepDisplayText = currentRep.substring(0, currentRepIdStartIndex);
            String currentRepId = currentRep.substring(currentRepIdStartIndex + 1, currentRep.length() - 1);

            //query rep pic with current id and set it below.
            Drawable unknownRepDrawable = getResources().getDrawable(R.drawable.unknown_representative);
            final int w = Math.max(1, 5);
            final int h = Math.max(1, 5);
            Bitmap repImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            //repImage.setImageDrawable(unknownRepDrawable);

            AsyncTask queryRepPics = new QueryRepPics(this, repData, currentRepId,currentRepDisplayText, adapter, repImage).execute();
            //ImageView defaultPic = picQuery.QueryPic(repData, currentRepId, currentRepDisplayText);


            //RepData.add(new Reps(defaultPic, currentRepDisplayText));

            //listOfReps.add(new Reps(R.drawable.foundingfathers1, currentRep));
        }
    }

    public void RepPicFoundAsyncCallback(Bitmap repPic, String currentRepDisplayText, LocalRepAdapter adapter){

        //Adapter myAdapter = (Adapter)findViewById(R.id.adap)
        Reps newRepData = new Reps(repPic, currentRepDisplayText);

        //RepData.add(new Reps(repPic, currentRepDisplayText));
        adapter.addAll(newRepData);
        adapter.notifyDataSetChanged();
    }


    public void DisplayData() { //ArrayList<Reps> repPicAndInfo

        //LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.mylist, RepData);

//        repsListView = (ListView) findViewById(R.id.listView);
//        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
//        repsListView.addHeaderView(header);
//        repsListView.setAdapter(adapter);
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


