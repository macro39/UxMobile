package sk.uxtweak.uxmobile.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by matej on 13.11.2017.
 */

public class NetworkUtils {
    public static final String TYPE_MOBILE = "mobile";
    public static final String TYPE_WIFI = "wifi";
    public static final String TYPE_MOBILE_DUN = "mobile_dun";
    public static final String TYPE_WIMAX = "wimax";
    public static final String TYPE_BLUETOOTH = "bluetooth";
    public static final String TYPE_DUMMY = "dummy";
    public static final String TYPE_ETHERNET = "ethernet";
    public static final String TYPE_VPN = "vpn";

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

    public static NetworkInfo getNetworkInfo(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return null;
        }

        return cm.getActiveNetworkInfo();
    }

    public static String getConnectivityString(Context context) {
        final NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo != null) {
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    return TYPE_MOBILE;
                case ConnectivityManager.TYPE_WIFI:
                    return TYPE_WIFI;
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    return TYPE_MOBILE_DUN;
                case ConnectivityManager.TYPE_WIMAX:
                    return TYPE_WIMAX;
                case ConnectivityManager.TYPE_BLUETOOTH:
                    return TYPE_BLUETOOTH;
                case ConnectivityManager.TYPE_DUMMY:
                    return TYPE_DUMMY;
                case ConnectivityManager.TYPE_ETHERNET:
                    return TYPE_ETHERNET;
                case ConnectivityManager.TYPE_VPN:
                    return TYPE_VPN;
            }
        }

        return "";
    }

    private NetworkUtils() {
    }
}
