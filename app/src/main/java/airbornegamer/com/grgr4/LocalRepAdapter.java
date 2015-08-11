package airbornegamer.com.grgr4;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

//http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
public class LocalRepAdapter extends ArrayAdapter<Reps> {

    Context context;
    int layoutResourceId;
    ArrayList<Reps> data = null;

    public LocalRepAdapter(Context context, int layoutResourceId, ArrayList<Reps> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RepsHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RepsHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);

            row.setTag(holder);
        } else {
            holder = (RepsHolder) row.getTag();
        }

        Reps reps = data.get(position);
        holder.txtTitle.setText(reps.title);
        holder.imgIcon.setImageBitmap(reps.pic);

        return row;
    }

    static class RepsHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }
}

