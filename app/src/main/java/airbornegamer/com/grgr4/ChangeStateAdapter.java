package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeStateAdapter extends ArrayAdapter<StatesRow> {

    Context context;
    int layoutResourceId;
    ArrayList<StatesRow> data;

    public ChangeStateAdapter(Context context, int layoutResourceId, ArrayList<StatesRow> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StatesHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StatesHolder();
            holder.statePic = (ImageView) row.findViewById(R.id.imgStatePic);
            holder.stateName = (TextView) row.findViewById(R.id.txtStateName);

            row.setTag(holder);
        } else {
            holder = (StatesHolder) row.getTag();
        }

        StatesRow statesRow = data.get(position);
        holder.stateName.setText(statesRow.StateName);
        holder.statePic.setImageDrawable(statesRow.StatePic);

        return row;
    }

    @Override
    public StatesRow getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    static class StatesHolder {
        ImageView statePic;
        TextView stateName;
    }
}
