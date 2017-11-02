package sk.brecka.uxmobile.model;

import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matej on 4.10.2017.
 */

public abstract class Event {
    private static final String TAG = "Event";

    protected static final int INDEX_TYPE = 0;
    protected static final int INDEX_TIME = 1;

    private long mTime;

    protected Event(long startTime) {
        mTime = System.currentTimeMillis() - startTime;
    }

    protected abstract String getType();

    public JSONArray toJson() throws JSONException {
        return new JSONArray()
                .put(INDEX_TYPE, getType())
                .put(INDEX_TIME, mTime);
    }
}