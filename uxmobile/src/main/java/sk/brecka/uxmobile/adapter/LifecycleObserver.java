package sk.brecka.uxmobile.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import sk.brecka.uxmobile.core.LifecycleCallback;

/**
 * Created by matej on 23.10.2017.
 */

public class LifecycleObserver implements Application.ActivityLifecycleCallbacks, ComponentCallbacks {
    private static final int ACTIVITY_END_TIMEOUT_MILLIS = 1_000;

    // TODO: su tieto fieldy vobec potrebne?
    private long mRecordingStart;
    private long mRecordingEnd;

    private int mActivityCounter;

    private LifecycleCallback mCallback;

    private Configuration mLatestConfiguration;

    public LifecycleObserver(LifecycleCallback callback) {
        mActivityCounter = 0;

        if (callback != null) {
            mCallback = callback;
        } else {
            mCallback = new LifecycleCallback() {
                @Override
                public void onFirstActivityStarted(Activity activity) {

                }

                @Override
                public void onEveryActivityStarted(Activity activity) {

                }

                @Override
                public void onEveryActivityStopped(Activity activity) {

                }

                @Override
                public void onConfigurationChanged(Configuration configuration) {

                }

                @Override
                public void onApplicationEnded() {

                }
            };
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        System.out.println("onActivityStarted " + activity.getLocalClassName());

        mActivityCounter++;

        if (isFirstActivity()) {
            mRecordingStart = System.currentTimeMillis();
            mCallback.onFirstActivityStarted(activity);
        }

        mCallback.onEveryActivityStarted(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        System.out.println("onActivityStopped " + activity.getLocalClassName());

        mActivityCounter--;

        mCallback.onEveryActivityStopped(activity);


        if (isLastActivity()) {
            System.out.println("isLast: " + isLastActivity() + " latestConf null: " + (mLatestConfiguration == null));
            mRecordingEnd = System.currentTimeMillis();
            mCallback.onApplicationEnded();
            mLatestConfiguration = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        System.out.println("onConfigurationChanged");

        mLatestConfiguration = newConfig;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        System.out.println("onActivityPaused " + activity.getLocalClassName());

        if (mLatestConfiguration != null) {
            mCallback.onConfigurationChanged(mLatestConfiguration);
//            mLatestConfiguration = null;
        }
    }

    //
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        System.out.println("onActivityCreated " + activity.getLocalClassName());
        //
    }

    @Override
    public void onActivityResumed(Activity activity) {
        System.out.println("onActivityResumed " + activity.getLocalClassName());

        //
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        System.out.println("onActivityDestroyed " + activity.getLocalClassName());
        //
    }

    @Override
    public void onLowMemory() {
        //
        System.out.println("Low memory");
    }

    //
    private boolean isFirstActivity() {
        return mActivityCounter == 1;
    }

    private boolean isLastActivity() {
        return mActivityCounter == 0;
    }
}
