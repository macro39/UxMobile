package sk.brecka.uxmobile.model.event;

import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matej on 4.10.2017.
 */

public abstract class Event {
    protected static final String TYPE_CUSTOM = "u";
    protected static final String TYPE_CLICK = "c";
    protected static final String TYPE_EXCEPTION = "e";
    protected static final String TYPE_FLING = "f";
    protected static final String TYPE_LONG_PRESS = "l";
    protected static final String TYPE_SCROLL = "s";
    protected static final String TYPE_ORIENTATION = "o";

    protected static final String TYPE_TASK = "t";

    protected static final String TYPE_SESSION_END = "x";   // dummy event

    protected static final int INDEX_TYPE = 0;
    protected static final int INDEX_TIME = 1;

    private final long mTime;

    protected Event(long startTime) {
        mTime = startTime;
    }

    protected abstract String getType();

    public JSONArray toJson() throws JSONException {
        return new JSONArray()
                .put(INDEX_TYPE, getType())
                .put(INDEX_TIME, mTime);
    }
}