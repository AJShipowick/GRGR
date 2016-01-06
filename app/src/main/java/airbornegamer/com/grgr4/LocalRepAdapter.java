package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
public class LocalRepAdapter extends ArrayAdapter<RepRow> {

    Context context;
    int layoutResourceId;
    ArrayList<RepRow> data = null;
    private ArrayList<Boolean> itemChecked = new ArrayList<>();

    public LocalRepAdapter(Context context, int layoutResourceId, ArrayList<RepRow> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = list;

        for (int i = 0; i < this.getCount(); i++) {
            itemChecked.add(i, this.data.get(i).isRepSelected); // checks items if they are user's rep
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
         final RepsHolder holder;//final

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

        RepRow repRow = data.get(position); //final
        holder.repPic.setImageDrawable(repRow.repPic);
        holder.repInfo.setText(repRow.repInfo);
        holder.repParty.setImageDrawable(repRow.repParty);
        holder.yourRep.setText(repRow.yourRep);
        holder.isRepSelected = false;

        final CheckBox cBox = (CheckBox) row.findViewById(R.id.chkContactRepresentative);
        cBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.chkContactRepresentative);
                if (cb.isChecked()) {
                    itemChecked.set(position, true);
                    holder.isRepSelected = true;
                } else {
                    itemChecked.set(position, false);
                    holder.isRepSelected = false;
                }
            }
        });

        cBox.setChecked(itemChecked.get(position));
        return row;
    }

    static class RepsHolder {
        ImageView repPic;
        TextView repInfo;
        ImageView repParty;
        TextView yourRep;
        Boolean isRepSelected;
    }
}

