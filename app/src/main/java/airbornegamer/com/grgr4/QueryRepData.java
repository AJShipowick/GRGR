//package airbornegamer.com.grgr4;
//
//import android.os.AsyncTask;
//
//import org.json.JSONObject;
//import org.json.JSONTokener;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class QueryRepData extends AsyncTask<String, String, String> {
//    JSONObject allRepData = new JSONObject();  //allRepData is populated with below call to a JSON object.
//    String queryURL = "";
//
//    public ActivityLocalReps activity;
//    public QueryRepData(ActivityLocalReps a, String url){
//        this.activity = a;
//        queryURL = url;
//    }
//
//    protected void onPostExecute(String result)
//    {
//        activity.buildRepresentativeData(allRepData);
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        HttpURLConnection urlConnection = null;
//
//        //todo build rep data from flat file here, it will change every 2 years.
//        try {
//            URL url = new URL(queryURL);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            BufferedReader bReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//            String temp, response = "";
//            while ((temp = bReader.readLine()) != null) {
//                response += temp;
//            }
//
//            allRepData = (JSONObject) new JSONTokener(response).nextValue();
//
//        } catch (Exception ex) {
//            String myEx = ex.toString();
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return null;
//    }
//}