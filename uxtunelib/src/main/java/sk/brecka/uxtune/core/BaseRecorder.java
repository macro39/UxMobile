package sk.brecka.uxtune.core;

import android.app.Activity;

import java.io.File;

/**
 * Created by matej on 25.8.2017.
 */

public abstract class BaseRecorder {

    protected final String TAG = getClass().getName();

    protected Activity mCurrentActivity;

    private long mRecordingStart;
    private long mRecordingEnd;

    private int mActivityCounter = 0;

    //
    public final void onActivityStarted(Activity activity) {
        mActivityCounter++;
        mCurrentActivity = activity;

        if (isFirstActivity()) {
            mRecordingStart = System.currentTimeMillis();
            onFirstActivityStarted(activity);
        }

        onEveryActivityStarted(activity);
    }

    public final void onActivityStopped(Activity activity) {
        mActivityCounter--;

        if (isLastActivity()) {
            mRecordingEnd = System.currentTimeMillis();
            onLastActivityStopped(activity);
        }
    }

    //
    protected void onFirstActivityStarted(Activity activity) {
        // intentionally blank
    }

    protected void onLastActivityStopped(Activity activity) {
        // intentionally blank
    }

    protected void onEveryActivityStarted(Activity activity) {
        // intentionally blank
    }

    //
    protected final boolean isFirstActivity() {
        return mActivityCounter == 1;
    }

    protected final boolean isLastActivity() {
        return mActivityCounter == 0;
    }

    public final boolean isRunning() {
        return mActivityCounter > 0;
    }

    //
    protected long getTimeFromStart(long current) {
        return current - mRecordingStart;
    }

    protected long getRecordingDuration() {
        return mRecordingEnd - mRecordingStart;
    }
}
