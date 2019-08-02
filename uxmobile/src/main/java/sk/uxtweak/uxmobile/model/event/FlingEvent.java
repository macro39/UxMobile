package sk.uxtweak.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 3.11.2017.
 */

public class FlingEvent extends TouchEvent {
    protected static final int INDEX_VELOCITY_X = 4;
    protected static final int INDEX_VELOCITY_Y = 5;

    private final double mVelocityX;
    private final double mVelocityY;

    public FlingEvent(long startTime, double x, double y, double velocityX, double velocityY) {
        super(startTime, x, y);
        mVelocityX = velocityX;
        mVelocityY = velocityY;
    }

    @Override
    protected String getType() {
        return TYPE_FLING;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_VELOCITY_X, mVelocityX)
                .put(INDEX_VELOCITY_Y, mVelocityY);
    }
}
