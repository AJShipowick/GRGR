package airbornegamer.com.grgr4;

//https://www.govtrack.us/
//https://www.govtrack.us/developers/api
//https://github.com/unitedstates/congress-legislators
//https://www.opencongress.org/

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityLocalReps extends Activity {

    LocalRepData repData = null;
    ListView listview;
    List<String> stateSpecificRepData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_reps);

        repData = new LocalRepData(this);
        String currentState = repData.getCurrentUsersState();

        if (currentState != null && repData.physicalStateIsKnown(currentState)) {
            String repURL = repData.buildCustomAPIURL(currentState);
            AsyncTask queryReps = new QueryRepData(this,repURL).execute();
        } else {
            //State not know, do something (audit...write msg to user....for us user only at this time....Manually select state....)
        }
    }

    public void filterRepData(JSONObject allRepData){
        stateSpecificRepData = repData.filterRepDataForUser(allRepData);
        DisplayData(stateSpecificRepData);
    }

     public void DisplayData(List<String> neededRepData) {

        List<Reps> listOfReps = new ArrayList<Reps>();
        for(int i = 0; i < neededRepData.size(); i++){
            String currentRep = neededRepData.get(i);
            listOfReps.add(new Reps(R.drawable.unknown_representative, currentRep));
            //new Reps(R.drawable.unknown_representative, currentRep);
        }

        Reps customRepData[] = new Reps[]{};
        customRepData = new Reps[]{
                new Reps(R.drawable.unknown_representative, listOfReps.get(0).title),
                new Reps(R.drawable.unknown_representative, listOfReps.get(1).title),
                new Reps(R.drawable.unknown_representative, listOfReps.get(2).title),
                new Reps(R.drawable.unknown_representative, listOfReps.get(3).title)
        };

        LocalRepAdapter adapter = new LocalRepAdapter(this, R.layout.mylist, customRepData);

        listview = (ListView)findViewById(R.id.listView);

        View header = (View)getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        listview.addHeaderView(header);

        listview.setAdapter(adapter);
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


