package k11.superac11.wifitransfer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class wifiordata {
    static String stats;


    public static String info(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Connected to WiFi
                    stats= "Wifi";
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // Connected to mobile data
                    stats= "Data";
                }
            } else {
                // No internet connection
                stats= "NoInternet";

            }
        } else {
            // Unable to get ConnectivityManager
            stats= "NoConnection";
        }
        return stats;
    }




}
