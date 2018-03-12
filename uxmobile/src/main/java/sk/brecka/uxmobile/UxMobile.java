package sk.brecka.uxmobile;

import android.app.Application;
import android.content.Context;

import java.util.Map;

/**
 * Created by matej on 4.10.2017.
 */

public class UxMobile {
    private static UxMobileSession sSession = null;

    public static void start(Context context, String apiKey) {
        start((Application) context.getApplicationContext(), apiKey);
    }

    public static void start(Application application, String apiKey) {
        if (sSession == null) {
            sSession = new UxMobileSession(application, apiKey);
        }
    }

    public static UxMobileSession getSession() {
        return sSession;
    }

    public static void addEvent(String eventName) {
        if (sSession != null) {
            sSession.addCustomEvent(eventName);
        } else {
            // exception?
        }
    }

    public static void addEvent(String eventName, Map<String, String> payload) {
        if (sSession != null) {
            sSession.addCustomEvent(eventName, payload);
        } else {
            // exception?
        }
    }

    private UxMobile() {
    }
}
