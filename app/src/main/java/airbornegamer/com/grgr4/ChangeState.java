package airbornegamer.com.grgr4;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

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

            States newState = new States(stateFullName);
            allStates.add(newState);
        }
        return allStates;
    }

    public void setStatesAdapter(ArrayList<States> allStates) {
        ChangeStateAdapter adapter = new ChangeStateAdapter(this, R.layout.list_states, allStates);
        ListView statesListView = (ListView) findViewById(R.id.listView_States);
        //View header = getLayoutInflater().inflate(R.layout.localreps_listview_header, null);
        //statesListView.addHeaderView(header);
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
