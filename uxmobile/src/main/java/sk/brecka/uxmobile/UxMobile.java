package sk.brecka.uxmobile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import sk.brecka.uxmobile.adapter.LifecycleObserver;
import sk.brecka.uxmobile.core.InputRecorder;
import sk.brecka.uxmobile.core.VideoRecorder;
import sk.brecka.uxmobile.net.RestClient;

/**
 * Created by matej on 4.10.2017.
 */

public class UxMobile {
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
