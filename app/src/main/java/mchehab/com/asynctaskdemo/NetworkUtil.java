package mchehab.com.asynctaskdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/**
 * Created by muhammadchehab on 11/4/17.
 */

public class NetworkUtil {

    /**
     * Checks if there's internet connection on the phone
     * @param context To initiate {@link ConnectivityManager}
     * @return True if network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected();
    }

    /**
     * Checks if Wifi is enabled
     * @param context To initiate {@link ConnectivityManager}
     * @return True if phone is using Wifi, false otherwise
     */
    public static boolean isWifiNetwork(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager
                .TYPE_WIFI;
    }

    /**
     * Helper method to enable Wifi
     * @param context To initiate {@link WifiManager}
     */
    public static void enableWifi(Context context){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }
}