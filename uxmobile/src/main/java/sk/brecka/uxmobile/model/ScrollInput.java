package sk.brecka.uxmobile.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 4.10.2017.
 */

public class ScrollInput extends Input {
    protected static final int INDEX_DISTANCE_X = 4;
    protected static final int INDEX_DISTANCE_Y = 5;

    private static final String TYPE_SCROLL = "s";

    private final float mDistanceX;
    private final float mDistanceY;

    public ScrollInput(float x, float y, float distanceX, float distanceY) {
        super(x, y);
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
