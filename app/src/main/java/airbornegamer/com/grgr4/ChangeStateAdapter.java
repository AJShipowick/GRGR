package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
            holder.statePic = (ImageView) row.findViewById(R.id.imgStatePic);
            holder.stateName = (TextView) row.findViewById(R.id.txtStateName);

            row.setTag(holder);
        } else {
            holder = (StatesHolder) row.getTag();
        }

        States states = data.get(position);
        holder.stateName.setText(states.StateName);
        holder.statePic.setImageBitmap(states.StatePic);

//        row.setClickable(true);
//        row.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ActivityLocalReps.class);
//                //intent.putExtras("StateName", v.Stat);
//                context.startActivity(intent);
//            }
//        });

        return row;
    }

    @Override
    public States getItem(int position) {
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
