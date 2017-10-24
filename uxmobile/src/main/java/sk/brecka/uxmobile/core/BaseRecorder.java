package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;

/**
 * Created by matej on 25.8.2017.
 */

public abstract class BaseRecorder implements LifecycleCallback {
    protected final String TAG = getClass().getName();

    protected Activity mCurrentActivity;

    //
    @Override
    public void onFirstActivityStarted(Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onEveryActivityStarted(Activity activity) {
        mCurrentActivity = activity;
    }

    @Override
    public void onEveryActivityStopped(Activity activity) {
        // intentionally blank
    }

    @Override
    public void onApplicationEnded() {
        // intentionally blank
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        // intentionally blank
    }
}
