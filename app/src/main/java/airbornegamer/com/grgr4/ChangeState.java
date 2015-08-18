package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//todo consider adding state flag as background to each state button.
public class ChangeState extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_state);

        try {
            new getAllStatesTask().execute().get();
        } catch (Exception ex) {
            //todo handle this
        }
    }

    private BitmapDrawable FindStateFlag(String stateName) {

        String imageStateName = stateName.toLowerCase();
        if (imageStateName.contains(" ")) {
            imageStateName = imageStateName.replace(" ", "_");
        }

        AssetManager assets = getApplicationContext().getResources().getAssets();
        try {
            InputStream buffer = new BufferedInputStream((assets.open(imageStateName + ".jpg")));
            Bitmap bitmap = BitmapFactory.decodeStream(buffer);
            return new BitmapDrawable(getApplicationContext().getResources(), bitmap);

        } catch (Exception ex) {
            //todo handle this
            return null;
        }
    }

    public void setStatesAdapter(ArrayList<StatesRow> allStates) {
        ChangeStateAdapter adapter = new ChangeStateAdapter(this, R.layout.list_states, allStates);
        ListView statesListView = (ListView) findViewById(R.id.listView_States);

        statesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ActivityLocalReps.class);

                StatesRow currentState = (StatesRow) parent.getAdapter().getItem(position);
                String stateName = currentState.StateName.toString();
                intent.putExtra("StateName", stateName);
                startActivity(intent);
                finish();
            }
        });

        statesListView.setAdapter(adapter);
    }

    private class getAllStatesTask extends AsyncTask<Void, Void, ArrayList<StatesRow>> {

        ArrayList<StatesRow> allStates = new ArrayList<StatesRow>();
        protected ArrayList<StatesRow> doInBackground(Void... params) {

            String[] knownStates = getApplicationContext().getResources().getStringArray(R.array.KnowStates);
            for (int i = 0; i < knownStates.length; i++) {
                String[] StatePair = knownStates[i].split(",");
                String stateFullName = StatePair[0];

                BitmapDrawable stateFlag = FindStateFlag(stateFullName);

                StatesRow newState = new StatesRow(stateFlag, stateFullName);
                allStates.add(newState);

            }
            return allStates;
        }

        @Override
        protected void onPostExecute(ArrayList<StatesRow> result) {
            setStatesAdapter(allStates);
        }
    }

    //http://developer.android.com/intl/ko/guide/topics/ui/menus.html
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_change_state, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            //return new thing here...
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}


