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

//    protected Activity mCurrentActivity;

    private long mRecordingStart;
    private long mRecordingEnd;

    private int mActivityCounter;

    private LifecycleCallback mCallback;

    private Handler mHandler = new Handler();
    private Timer mActivityTransitionTimer;
    private final Runnable mCheckBackgroundRunnable = new Runnable() {
        @Override
        public void run() {
            if (mActivityCounter <= 0 && mCallback != null) {
                mCallback.onApplicationEnded();
            }
        }
    };

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
                public void onApplicationEnded() {

                }
            };
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityStarted");

//        mActivityCounter++;
////        mCurrentActivity = activity;
//
//        if (isFirstActivity()) {
//            mRecordingStart = System.currentTimeMillis();
//            mCallback.onFirstActivityStarted(activity);
//        }
//
//        mCallback.onEveryActivityStarted(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityStopped");

//        mActivityCounter--;
//
//        if (isLastActivity()) {
//            mRecordingEnd = System.currentTimeMillis();
//        }
//
//        mCallback.onEveryActivityStopped(activity);
//
//        // timer
//        mHandler.postDelayed(mCheckBackgroundRunnable, ACTIVITY_END_TIMEOUT_MILLIS);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        System.out.println("Configuration changed");
    }

    //
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityCreated");
        //
    }

    @Override
    public void onActivityResumed(Activity activity) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityResumed");
        //
    }

    @Override
    public void onActivityPaused(Activity activity) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityPaused");
        //
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        System.out.println(activity.getClass().getSimpleName() + " onActivitySaveInstanceState");
        //
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        System.out.println(activity.getClass().getSimpleName() + " onActivityDestroyed");
        //
    }

    @Override
    public void onLowMemory() {
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
