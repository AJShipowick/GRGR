package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

//todo consider adding state flag as background to each state button.
public class ChangeState extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_state);

        ArrayList<States> allStates = FillListWithKnowStates();
        setStatesAdapter(allStates);

        //set on click listeners for the selected state.
        //http://stackoverflow.com/questions/4401028/dynamically-creating-buttons-and-setting-onclicklistener

    }

    public ArrayList<States> FillListWithKnowStates() {
        ArrayList<States> allStates = new ArrayList<States>();

        String[] knownStates = this.getResources().getStringArray(R.array.KnowStates);

        for (int i = 0; i < knownStates.length; i++) {
            String[] StatePair = knownStates[i].split(",");
            String stateFullName = StatePair[0];

            Bitmap stateFlag = FindStateFlag(stateFullName);

            States newState = new States(stateFlag, stateFullName);
            allStates.add(newState);

            //setupChangeStateEventListener();
        }
        return allStates;

    }

//    public void changeStateForTheUser(){
//
//
//        int someint = 4;
//
//    }

    private Bitmap FindStateFlag(String stateName) {

        String imageStateName = stateName.toLowerCase();
        if (imageStateName.contains(" ")) {
            imageStateName = imageStateName.replace(" ", "_");
        }

        int imgID = getApplicationContext().getResources().getIdentifier(getApplicationContext().getPackageName() + ":drawable/" + imageStateName, null, null);

        return BitmapFactory.decodeResource(getResources(), imgID);
    }

    public void setStatesAdapter(ArrayList<States> allStates) {
        ChangeStateAdapter adapter = new ChangeStateAdapter(this, R.layout.list_states, allStates);
        ListView statesListView = (ListView) findViewById(R.id.listView_States);
        //View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        //statesListView.addHeaderView(header);

        statesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ActivityLocalReps.class);

                States currentState = (States) parent.getAdapter().getItem(position);
                String stateName = currentState.StateName.toString();
                intent.putExtra("StateName", stateName);

                //todo need newtask and finish?
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                //finish();
            }
        });

        statesListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_state, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
