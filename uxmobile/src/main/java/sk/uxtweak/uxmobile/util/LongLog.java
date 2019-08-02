package sk.uxtweak.uxmobile.util;

import android.util.Log;

/**
 * Created by matej on 7.2.2018.
 */

public class LongLog {

    public static void d(String tag, String message) {
        int maxLogSize = 1000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(tag, message.substring(start, end));
        }
    }
}
