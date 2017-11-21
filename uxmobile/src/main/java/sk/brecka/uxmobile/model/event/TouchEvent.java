package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

import sk.brecka.uxmobile.model.event.Event;

/**
 * Created by matej on 24.10.2017.
 */

public abstract class TouchEvent extends Event {
    protected static final int INDEX_X = 2;
    protected static final int INDEX_Y = 3;

    private final double mX;
    private final double mY;

    protected TouchEvent(long startTime, double x, double y) {
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
