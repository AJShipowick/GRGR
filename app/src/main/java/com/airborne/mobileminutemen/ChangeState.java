package com.airborne.mobileminutemen;

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

            //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 300, false);

            return new BitmapDrawable(getApplicationContext().getResources(), bitmap);

        } catch (Exception ex) {
            //todo handle this
            return null;
        }
    }

    public void setStatesAdapter(ArrayList<StatesRow> allStates) {
        ChangeStateAdapter adapter = new ChangeStateAdapter(this, R.layout.list_all_states, allStates);
        ListView statesListView = (ListView) findViewById(R.id.listView_States);

        statesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ActivityLocalReps.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                StatesRow currentState = (StatesRow) parent.getAdapter().getItem(position);
                String stateName = currentState.StateName;
                intent.putExtra("StateName", stateName);
                startActivity(intent);
                finish();
            }
        });

        statesListView.setAdapter(adapter);
    }

    private class getAllStatesTask extends AsyncTask<Void, Void, ArrayList<StatesRow>> {

        ArrayList<StatesRow> allStates = new ArrayList<>();
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
}


