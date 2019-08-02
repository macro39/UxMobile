package sk.uxtweak.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;

/**
 * Created by matej on 25.8.2017.
 */

public abstract class BaseRecorder implements LifecycleCallback {
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
    public void onLastActivityStopped(Activity activity) {
        // intentionally blank
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        // intentionally blank
    }

    public void onSessionStarted(){
        // intentionally blank
    }
}
