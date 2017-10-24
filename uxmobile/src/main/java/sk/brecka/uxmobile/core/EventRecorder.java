package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import sk.brecka.uxmobile.adapter.WindowCallbackAdapter;
import sk.brecka.uxmobile.model.EventRecording;
import sk.brecka.uxmobile.model.ViewEnum;


/**
 * Created by matej on 25.8.2017.
 */

public class EventRecorder extends BaseRecorder implements GestureDetector.OnGestureListener {

    // TODO: flushovat recordingy po odoslani
    // TODO: nech otocenie nerozdeli nahravania
    // TODO: mozno na eventy spravit factory a rovno JSON namiesto tejto hierarchie

    private LinkedList<EventRecording> mEventRecordings = new LinkedList<>();
    private GestureDetector mGestureDetector;

    @Override
    public void onEveryActivityStarted(Activity activity) {
        super.onEveryActivityStarted(activity);

        // recordni
        mEventRecordings.addLast(new EventRecording(activity));

        //
        Window.Callback previousCallback = activity.getWindow().getCallback();

        // zabrani kopeniu
        if (previousCallback instanceof WindowCallbackAdapter) {
            return;
        }

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

    // gesture listener metody
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        mEventRecordings.getLast().addClickInput((int) e.getX(), (int) e.getY(), ViewEnum.fromView(touchedView), getViewInfo(touchedView));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        mEventRecordings.getLast().addLongPressInput((int) e.getX(), (int) e.getY(), ViewEnum.fromView(touchedView), getViewInfo(touchedView));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mEventRecordings.getLast().addScrollinput((int) e2.getX(), (int) e2.getY(), (int) distanceX, (int) distanceY);
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);

        // TODO: kontrolovat ci dojde k zmene orientacie
        mEventRecordings.getLast().addOrientationinput(configuration.orientation);
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

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // intentionally blank
        return false;
    }

    // TODO: presunut do samostatnej classy
    private View getTouchedView(MotionEvent motionEvent) {
        return rGetTouchedView(mCurrentActivity.getWindow().getDecorView().getRootView(), motionEvent, new Rect());
    }

    private View rGetTouchedView(View view, MotionEvent motionEvent, Rect rect) {
        // TODO: co ak sa viewy overlapuju?
        if (!view.isShown() || view.getWidth() == 0 || view.getHeight() == 0) {
            return null;
        }

        view.getGlobalVisibleRect(rect);

        final int x = (int) motionEvent.getX(motionEvent.getActionIndex());
        final int y = (int) motionEvent.getY(motionEvent.getActionIndex());

        if (rect.contains(x, y)) {
            if (view instanceof ViewGroup) {
                //
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    final View out = rGetTouchedView(((ViewGroup) view).getChildAt(i), motionEvent, rect);
                    if (out != null) {
                        return out;
                    }
                }
            } else {
                return view;
            }
        }

        return null;
    }

    private String getViewInfo(final View view) {
        if (view == null) {
            return "";
        } else if (view instanceof EditText) {
            return "";
        } else if (view instanceof Button) {
            return ((Button) view).getText().toString();
        } else if (view instanceof SeekBar) {
            return String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof ImageButton) {
            return "";
        } else {
            return "";
        }
    }

    public JSONArray getOutput() throws JSONException{
        JSONArray out = new JSONArray();

        for (EventRecording recording : mEventRecordings) {
                out.put(recording.toJson());
        }

        return out;
    }
}
