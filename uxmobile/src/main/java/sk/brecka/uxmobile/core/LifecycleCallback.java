package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;

/**
 * Created by matej on 23.10.2017.
 */

public interface LifecycleCallback {
    void onFirstActivityStarted(Activity activity);

    void onEveryActivityStarted(Activity activity);

    void onEveryActivityStopped(Activity activity);

    void onConfigurationChanged(Configuration configuration);

    void onApplicationEnded();
}
