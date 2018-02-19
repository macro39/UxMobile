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
import sk.brecka.uxmobile.model.event.ExceptionEvent;
import sk.brecka.uxmobile.model.event.ClickEvent;
import sk.brecka.uxmobile.model.EventRecording;
import sk.brecka.uxmobile.model.ViewEnum;
import sk.brecka.uxmobile.model.event.CustomEvent;
import sk.brecka.uxmobile.model.event.Event;
import sk.brecka.uxmobile.model.event.FlingEvent;
import sk.brecka.uxmobile.model.event.LongPressEvent;
import sk.brecka.uxmobile.model.event.OrientationEvent;
import sk.brecka.uxmobile.model.event.ScrollEvent;
import sk.brecka.uxmobile.model.event.SessionEndEvent;
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

    private long mStartTime;

    @Override
    public void onFirstActivityStarted(Activity activity) {
        super.onFirstActivityStarted(activity);

        mStartTime = System.currentTimeMillis();

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
            mEventRecordings.addLast(new EventRecording(activity, currentRelativeMillis()));
        }

        //
        mConfigurationRecentlyChanged = false;

        //
        Window.Callback previousCallback = activity.getWindow().getCallback();

        // prevents cumulation
        if (previousCallback instanceof WindowCallbackAdapter) {
            return;
        }

        activity.getWindow().setCallback(new WindowCallbackAdapter(previousCallback) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return super.dispatchTouchEvent(event);
            }
        });

        //
        mGestureDetector = new GestureDetector(activity, this);
    }

    @Override
    public void onLastActivityStopped(Activity activity) {
        super.onLastActivityStopped(activity);
        recordEvent(new SessionEndEvent(currentRelativeMillis()));
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        mConfigurationRecentlyChanged = true;

        if (mOrientation != configuration.orientation) {
            mOrientation = configuration.orientation;

            recordEvent(new OrientationEvent(currentRelativeMillis(), mOrientation));
        }
    }

    // gesture listener metody
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        final View rootView = getRootView();
        final View touchedView = ViewUtils.getTouchedView(e, rootView);

        recordEvent(new ClickEvent(currentRelativeMillis(),
                e.getX() / rootView.getWidth(),
                e.getY() / rootView.getHeight(),
                ViewEnum.fromView(touchedView),
                ViewUtils.getViewText(touchedView),
                ViewUtils.getViewValue(touchedView)
        ));

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final View rootView = getRootView();
        final View touchedView = ViewUtils.getTouchedView(e, rootView);

        recordEvent(new LongPressEvent(currentRelativeMillis(),
                e.getX() / rootView.getWidth(),
                e.getY() / rootView.getHeight(),
                ViewEnum.fromView(touchedView),
                ViewUtils.getViewText(touchedView),
                ViewUtils.getViewValue(touchedView)
        ));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final View rootView = getRootView();

        recordEvent(new ScrollEvent(currentRelativeMillis(),
                e2.getX() / rootView.getWidth(),
                e2.getY() / rootView.getHeight(),
                distanceX / rootView.getWidth(),
                distanceY / rootView.getHeight()
        ));

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final View rootView = getRootView();

        recordEvent(new FlingEvent(currentRelativeMillis(),
                e2.getX() / rootView.getWidth(),
                e2.getY() / rootView.getHeight(),
                velocityX / rootView.getWidth(),
                velocityY / rootView.getHeight()
        ));

        return false;
    }

    public void addEvent(String eventName) {
        recordEvent(new CustomEvent(currentRelativeMillis(), eventName));
    }

    public void addExceptionEvent(Throwable throwable) {
        recordEvent(new ExceptionEvent(currentRelativeMillis(), throwable));
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
    private void recordEvent(Event event) {
        mEventRecordings.getLast().addEvent(event);
    }

    private long currentRelativeMillis() {
        return System.currentTimeMillis() - mStartTime;
    }

    private View getRootView() {
        return mCurrentActivity.getWindow().getDecorView().getRootView();
    }

    public JSONArray getOutput() throws JSONException {
        JSONArray out = new JSONArray();

        for (EventRecording recording : mEventRecordings) {
            out.put(recording.toJson());
        }

        return out;
    }
}
