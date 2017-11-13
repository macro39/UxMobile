package sk.brecka.uxmobile.model.event;

import android.app.Activity;
import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.brecka.uxmobile.model.ViewEnum;

/**
 * Created by matej on 30.8.2017.
 */

public class EventRecording {
    private static final String TAG_ACTIVITY_NAME = "activity_name";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_START_TIME = "start_time";

    private final String mActivityName;
    private final long mStart;
    private final List<Event> mEvents = new ArrayList<>();

    public EventRecording(String activityName, long start) {
        mActivityName = activityName;
        mStart = start;
    }

    public EventRecording(Activity activity) {
        this(activity.getLocalClassName(), System.currentTimeMillis());
    }

    public void addClickInput(MotionEvent motionEvent, ViewEnum viewEnum, String viewText, String viewString) {
        mEvents.add(new ClickEvent(mStart, (int) motionEvent.getX(), (int) motionEvent.getY(), viewEnum, viewText, viewString));
    }

    public void addLongPressInput(MotionEvent motionEvent, ViewEnum viewEnum, String viewText, String viewString) {
        mEvents.add(new LongPressEvent(mStart, (int) motionEvent.getX(), (int) motionEvent.getY(), viewEnum, viewText, viewString));
    }

    public void addScrollinput(MotionEvent motionEvent, float distanceX, float distanceY) {
        mEvents.add(new ScrollEvent(mStart, (int) motionEvent.getX(), (int) motionEvent.getY(), (int) distanceX, (int) distanceY));
    }

    public void addFlinginput(MotionEvent motionEvent, float velocityX, float velocityY) {
        mEvents.add(new FlingEvent(mStart, (int) motionEvent.getX(), (int) motionEvent.getY(), (int) velocityX, (int) velocityY));
    }

    public void addOrientationinput(int orientation) {
        mEvents.add(new OrientationEvent(mStart, orientation));
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(TAG_ACTIVITY_NAME, mActivityName)
                .put(TAG_START_TIME, mStart)
                .put(TAG_EVENTS, inputsToJson());
    }

    private JSONArray inputsToJson() throws JSONException {
        JSONArray out = new JSONArray();

        for (int i = 0; i < mEvents.size(); i++) {
            out.put(i, mEvents.get(i).toJson());
        }

        return out;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventRecording that = (EventRecording) o;

        if (mStart != that.mStart) return false;
        return mActivityName != null ? mActivityName.equals(that.mActivityName) : that.mActivityName == null;

    }

    @Override
    public int hashCode() {
        int result = mActivityName != null ? mActivityName.hashCode() : 0;
        result = 31 * result + (int) (mStart ^ (mStart >>> 32));
        return result;
    }
}
