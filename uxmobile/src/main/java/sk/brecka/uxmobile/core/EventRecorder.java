package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import sk.brecka.uxmobile.adapter.WindowCallbackAdapter;
import sk.brecka.uxmobile.model.event.EventRecording;
import sk.brecka.uxmobile.model.ViewEnum;
import sk.brecka.uxmobile.util.ViewUtils;


/**
 * Created by matej on 25.8.2017.
 */

public class EventRecorder extends BaseRecorder implements GestureDetector.OnGestureListener {
    // LinkedList for simpler retrieval of the latest recording
    private LinkedList<EventRecording> mEventRecordings = new LinkedList<>();
    private GestureDetector mGestureDetector;

    private int mOrientation;
    private boolean mConfigurationRecentlyChanged;

    @Override
    public void onFirstActivityStarted(Activity activity) {
        super.onFirstActivityStarted(activity);

        // init
        mConfigurationRecentlyChanged = false;
        mOrientation = activity.getResources().getConfiguration().orientation;
        mEventRecordings.clear();
    }

    @Override
    public void onEveryActivityStarted(Activity activity) {
        super.onEveryActivityStarted(activity);

        // record
        if (!mConfigurationRecentlyChanged) {
            mEventRecordings.addLast(new EventRecording(activity));
        }

        //
        mConfigurationRecentlyChanged = false;

        //
        Window.Callback previousCallback = activity.getWindow().getCallback();

        // prevents cumulation
        if (previousCallback instanceof WindowCallbackAdapter) {
            return;
        }

        //
        mGestureDetector = new GestureDetector(activity, this);

        activity.getWindow().setCallback(new WindowCallbackAdapter(previousCallback) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return super.dispatchTouchEvent(event);
            }
        });
    }

    @Override
    public void onLastActivityStopped(Activity activity) {
        super.onLastActivityStopped(activity);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        mConfigurationRecentlyChanged = true;

        if (mOrientation != configuration.orientation) {
            mOrientation = configuration.orientation;
            getLastRecording().addOrientationinput(configuration.orientation);
        }
    }

    // gesture listener metody
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        getLastRecording().addClickInput(e, ViewEnum.fromView(touchedView), ViewUtils.getViewText(touchedView), ViewUtils.getViewValue(touchedView));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        getLastRecording().addLongPressInput(e, ViewEnum.fromView(touchedView), ViewUtils.getViewText(touchedView), ViewUtils.getViewValue(touchedView));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        getLastRecording().addScrollinput(e2, distanceX, distanceY);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        getLastRecording().addFlinginput(e2, velocityX, velocityY);
        return false;
    }

    //
    @Override
    public boolean onDown(MotionEvent e) {
        // intentionally blank
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // intentionally blank
    }

    //
    private EventRecording getLastRecording() {
        return mEventRecordings.getLast();
    }

    private View getTouchedView(MotionEvent e) {
        return ViewUtils.getTouchedView(e, mCurrentActivity.getWindow().getDecorView().getRootView());
    }

    public JSONArray getOutput() throws JSONException {
        JSONArray out = new JSONArray();

        for (EventRecording recording : mEventRecordings) {
            out.put(recording.toJson());
        }

        return out;
    }
}
