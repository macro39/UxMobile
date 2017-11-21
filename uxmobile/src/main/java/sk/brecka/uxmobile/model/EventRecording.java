package sk.brecka.uxmobile.model;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sk.brecka.uxmobile.model.event.Event;

/**
 * Created by matej on 30.8.2017.
 */

public class EventRecording {
    private static final String TAG_ACTIVITY_NAME = "activity_name";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_START_TIME = "start_time";

    private final String mActivityName;
    private final long mStartTime;
    private final List<Event> mEvents = new ArrayList<>();

    public EventRecording(String activityName, long startTime) {
        mActivityName = activityName;
        mStartTime = startTime;
    }

    public EventRecording(Activity activity, long startTime) {
        this(activity.getLocalClassName(), startTime);
    }

    public void addEvent(Event event) {
        mEvents.add(event);
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(TAG_ACTIVITY_NAME, mActivityName)
                .put(TAG_START_TIME, mStartTime)
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

        if (mStartTime != that.mStartTime) return false;
        return mActivityName != null ? mActivityName.equals(that.mActivityName) : that.mActivityName == null;

    }

    @Override
    public int hashCode() {
        int result = mActivityName != null ? mActivityName.hashCode() : 0;
        result = 31 * result + (int) (mStartTime ^ (mStartTime >>> 32));
        return result;
    }
}
