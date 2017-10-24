package sk.brecka.uxmobile.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;

import sk.brecka.uxmobile.core.LifecycleCallback;

/**
 * Created by matej on 23.10.2017.
 */

public class LifecycleObserver implements Application.ActivityLifecycleCallbacks, ComponentCallbacks {

    private boolean mIsResumed = false;
    private boolean mIsCreated = false;
    private boolean mIsStarted = false;

    //
    private int mActivityCounter;
    private boolean mAnyActivityStarted = false;
    private boolean mPausedWithConfig = false;

    private Configuration mLatestConfiguration;

    private LifecycleCallback mCallback;

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
                public void onLastActivityStopped(Activity activity) {

                }
            };
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mIsStarted = true;
        mLatestConfiguration = null;

        mActivityCounter++;

        if (isFirstActivity() && !mAnyActivityStarted) {
            mCallback.onFirstActivityStarted(activity);

            mAnyActivityStarted = true;
        }

        mCallback.onEveryActivityStarted(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mIsStarted = false;

        mActivityCounter--;

        mCallback.onEveryActivityStopped(activity);

        if (isLastActivity() && !mPausedWithConfig) {
            mCallback.onLastActivityStopped(activity);

            mLatestConfiguration = null;
            mAnyActivityStarted = false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mLatestConfiguration = newConfig;
        if (mIsResumed) {
            mCallback.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mIsResumed = false;

        mPausedWithConfig = (mLatestConfiguration != null);
    }

    //
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mIsCreated = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mIsResumed = true;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mIsCreated = false;
    }

    @Override
    public void onLowMemory() {
        //
    }

    //
    private boolean isFirstActivity() {
        return mActivityCounter == 1;
    }

    private boolean isLastActivity() {
        return mActivityCounter == 0;
    }
}
