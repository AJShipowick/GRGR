package airbornegamer.com.grgr4;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.webkit.URLUtil;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;

public class QueryRepPics extends AsyncTask<String, String, String> {

    public ActivityLocalReps activity;
    LocalRepData repData = null;
    Bitmap defaultPic;
    String id = "";
    String currentRepDisplayText = "";
    LocalRepAdapter adapter;

    public QueryRepPics(ActivityLocalReps a, LocalRepData repData, String id, String currentRepDisplayText, LocalRepAdapter adapter, Bitmap defaultPic) {
        this.activity = a;
        this.repData = repData;
        this.id = id;
        this.currentRepDisplayText = currentRepDisplayText;
        this.adapter = adapter;
        this.defaultPic = defaultPic;
    }

    protected void onPostExecute(String result) {
        activity.RepPicFoundAsyncCallback(defaultPic, currentRepDisplayText, adapter);
    }

    @Override
    protected String doInBackground(String... params) {
        //ImageView defaultPic = null;
        String repURL = repData.buildCustomPicAPIURL(id);

        try {

            URL imageURL = new URL(repURL);
            defaultPic = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        } catch (Exception ex) {
            defaultPic = BitmapFactory.decodeResource(activity.getResources(), R.drawable.unknown_representative);
            //String myEx = ex.toString();
        }
        return null;
    }
}


//    public ImageView QueryPic(LocalRepData repData, String id, String currentRepDisplayText) {
//
//        ImageView defaultPic = null;
//        String repURL = repData.buildCustomPicAPIURL(id);
//
//        try {
//            URL imageURL = new URL(repURL);
//            Bitmap myBitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//            defaultPic.setImageBitmap(myBitmap);
//
//            return defaultPic;
//        } catch (Exception ex) {
//            String myEx = ex.toString();
//        }
//
//        return null;
//    }
