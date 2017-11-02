package sk.brecka.uxmobile.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 24.10.2017.
 */

public abstract class TouchEvent extends Event {
    private static final String TAG = "TouchEvent";

    protected static final int INDEX_X = 2;
    protected static final int INDEX_Y = 3;

    private final int mX;
    private final int mY;

    protected TouchEvent(long startTime, int x, int y) {
        super(startTime);
        mX = x;
        mY = y;
    }

    protected abstract String getType();

    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_X, mX)
                .put(INDEX_Y, mY);
    }
}
