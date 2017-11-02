package sk.brecka.uxmobile;

import android.app.Application;
import android.content.Context;

/**
 * Created by matej on 4.10.2017.
 */

public class UxMobile {
    private static final String TAG = "UxMobile";

    private static UxMobileSession sSession = null;

    public static void start(Context context) {
        start((Application) context.getApplicationContext());
    }

    public static void start(Application application) {
        if (sSession == null) {
            sSession = new UxMobileSession(application);
        }
    }

    private UxMobile() {
    }
}
