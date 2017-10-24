package sk.brecka.uxmobile.model;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matej on 30.8.2017.
 */

public class EventRecording {

    private static final String TAG_ACTIVITY_NAME = "activity_name";
    private static final String TAG_INPUTS = "inputs";
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

    public void addClickInput(int x, int y, ViewEnum viewEnum, String viewString) {
        mEvents.add(new ClickEvent(x, y, mStart, viewEnum, viewString));
    }

    public void addLongPressInput(int x, int y, ViewEnum viewEnum, String viewString) {
        mEvents.add(new LongPressEvent(x, y, mStart, viewEnum, viewString));
    }

    public void addScrollinput(int x, int y, int distanceX, int distanceY) {
        mEvents.add(new ScrollEvent(x, y, mStart, distanceX, distanceY));
    }

    public void addOrientationinput(int orientation) {
        mEvents.add(new OrientationEvent(mStart, orientation));
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(TAG_ACTIVITY_NAME, mActivityName)
                .put(TAG_START_TIME, mStart)
                .put(TAG_INPUTS, inputsToJson());
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
