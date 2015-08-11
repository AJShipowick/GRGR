package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

        repData = new LocalRepData(this);
        String currentState = repData.getCurrentUsersState();

        if (currentState != null && repData.physicalStateIsKnown(currentState)) {
            String repURL = repData.buildCustomDataAPIURL(currentState);
            AsyncTask queryReps = new QueryRepData(this, repURL).execute();
        } else {
            //State not know, do something (audit...write msg to user....for us user only at this time....Manually select state....)
        }
    }

    public void setupBtnToChangeStatesListener(){
        Button btnChangeStates = (Button)findViewById(R.id.btnChangeState);
        btnChangeStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangeState.class);
                startActivity(intent);
            }
        });
    }


    //Callback method after AsyncTask queryReps completes!
    //https://www.govtrack.us/developers/data
    //https://www.govtrack.us/data/photos/
    public void filterRepData(JSONObject allRepData) {
        stateSpecificRepData = repData.filterRepDataForUser(allRepData);
        LocalRepAdapter adapter = SetupAdapter();
        CombineRepInfoAndPhoto(repData, stateSpecificRepData, adapter);
    }

    ArrayList<Reps> RepData = new ArrayList<Reps>();
    public LocalRepAdapter SetupAdapter(){
        LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.list_reps, RepData);
        repsListView = (ListView) findViewById(R.id.listView_Reps);
        View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        repsListView.addHeaderView(header);
        repsListView.setAdapter(adapter);

        setupBtnToChangeStatesListener();

        return adapter;
    }

    public void CombineRepInfoAndPhoto(LocalRepData repData, List<String> stateSpecificRepData, LocalRepAdapter adapter) {

        for (int i = 0; i < stateSpecificRepData.size(); i++) {
            String currentRep = stateSpecificRepData.get(i);
            int currentRepIdStartIndex = currentRep.indexOf("(");

            String currentRepDisplayText = currentRep.substring(0, currentRepIdStartIndex);
            String currentRepId = currentRep.substring(currentRepIdStartIndex + 1, currentRep.length() - 1);

            final int w = Math.max(1, 5);
            final int h = Math.max(1, 5);
            Bitmap repImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

            AsyncTask queryRepPics = new QueryRepPics(this, repData, currentRepId,currentRepDisplayText, adapter, repImage).execute();
        }
    }

    public void RepPicFoundAsyncCallback(Bitmap repPic, String currentRepDisplayText, LocalRepAdapter adapter){
        Reps newRepData = new Reps(repPic, currentRepDisplayText);
        adapter.addAll(newRepData);
        adapter.notifyDataSetChanged();
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


