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

//http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
public class LocalRepAdapter extends ArrayAdapter<RepRow> {

    Context context;
    int layoutResourceId;
    ArrayList<RepRow> data = null;

    public LocalRepAdapter(Context context, int layoutResourceId, ArrayList<RepRow> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = list;
    }

    //todo Fix check box problem:
    //http://stackoverflow.com/questions/11190390/checking-a-checkbox-in-listview-makes-other-random-checkboxes-checked-too

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RepsHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RepsHolder();
            holder.repPic = (ImageView) row.findViewById(R.id.imgRep);
            holder.repInfo = (TextView) row.findViewById(R.id.txtRepInfo);
            holder.repParty = (ImageView) row.findViewById(R.id.imgRepParty);
            holder.yourRep = (TextView) row.findViewById(R.id.txtYourRep);

            row.setTag(holder);
        } else {
            holder = (RepsHolder) row.getTag();
        }

        RepRow repRow = data.get(position);
        holder.repPic.setImageDrawable(repRow.repPic);
        holder.repInfo.setText(repRow.repInfo);
        holder.repParty.setImageDrawable(repRow.repParty);
        holder.yourRep.setText(repRow.yourRep);

        return row;
    }

    static class RepsHolder {
        ImageView repPic;
        TextView repInfo;
        ImageView repParty;
        TextView yourRep;
    }
}

