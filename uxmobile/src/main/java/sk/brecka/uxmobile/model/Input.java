package sk.brecka.uxmobile.model;

import android.view.MotionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matej on 4.10.2017.
 */

public abstract class Input {
    protected static final int INDEX_TYPE = 0;
    protected static final int INDEX_X = 1;
    protected static final int INDEX_Y = 2;
    protected static final int INDEX_TIME = 3;

    private final float mX;
    private final float mY;
    private final long mTime;

    protected Input(float x, float y) {
        mX = x;
        mY = y;
        mTime = System.currentTimeMillis();
    }

    protected abstract String getType();

    public JSONArray toJson() throws JSONException{
        return new JSONArray()
                .put(INDEX_TYPE,getType())
                .put(INDEX_X, mX)
                .put(INDEX_Y, mY)
                .put(INDEX_TIME, mTime);
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public long getTime() {
        return mTime;
    }
}