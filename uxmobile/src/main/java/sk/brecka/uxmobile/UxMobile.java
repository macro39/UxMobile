package sk.brecka.uxmobile;

import android.app.Application;
import android.content.Context;

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

    private UxMobile() {
    }
}
