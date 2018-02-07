package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 4.10.2017.
 */

public class ScrollEvent extends TouchEvent {
    protected static final int INDEX_DISTANCE_X = 4;
    protected static final int INDEX_DISTANCE_Y = 5;

    private final double mDistanceX;
    private final double mDistanceY;

    public ScrollEvent(long startTime, double x, double y, double distanceX, double distanceY) {
        super(startTime, x, y);
        mDistanceX = distanceX;
        mDistanceY = distanceY;
    }

    @Override
    protected String getType() {
        return TYPE_SCROLL;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_DISTANCE_X, mDistanceX)
                .put(INDEX_DISTANCE_Y, mDistanceY);
    }
}
