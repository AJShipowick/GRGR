package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class ChangeStateAdapter extends ArrayAdapter<States> {

    Context context;
    int layoutResourceId;
    ArrayList<States> data;

    public ChangeStateAdapter(Context context, int layoutResourceId, ArrayList<States> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StatesHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StatesHolder();
            holder.State = (Button) row.findViewById(R.id.btnNewStateSelection);

            row.setTag(holder);
        } else {
            holder = (StatesHolder) row.getTag();
        }

        States states = data.get(position);
        holder.State.setText(states.StateName);

        return row;
    }

    static class StatesHolder {
        Button State;
    }
}
