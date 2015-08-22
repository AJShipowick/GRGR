package airbornegamer.com.grgr4;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectivity {

    public InternetConnectivity(){
    }

    public boolean isConnected(Context appContext){
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(appContext.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null){return false;};
        return cm.getActiveNetworkInfo().isConnected();
    }
}
