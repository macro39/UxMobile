package sk.brecka.uxmobile.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by matej on 13.11.2017.
 */

public class NetworkUtils {

    public static boolean hasUnlimitedConnection(NetworkInfo networkInfo) {
        return (hasWifiConnection(networkInfo) || hasEthernetConnection(networkInfo));
    }

    public static boolean hasUnlimitedConnection(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        return (hasWifiConnection(networkInfo) || hasEthernetConnection(networkInfo));
    }

    public static boolean hasWifiConnection(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean hasWifiConnection(NetworkInfo networkInfo) {
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean hasMobileConnection(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean hasMobileConnection(NetworkInfo networkInfo) {
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean hasEthernetConnection(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET);
    }

    public static boolean hasEthernetConnection(NetworkInfo networkInfo) {
        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET);
    }

    public static boolean hasConnection(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static boolean hasConnection(NetworkInfo networkInfo) {
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return null;
        }

        return cm.getActiveNetworkInfo();
    }

    private NetworkUtils() {
    }
}
