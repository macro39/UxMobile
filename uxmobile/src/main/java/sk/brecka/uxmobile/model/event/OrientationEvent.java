package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

import sk.brecka.uxmobile.model.event.Event;

/**
 * Created by matej on 24.10.2017.
 */

public class OrientationEvent extends Event {
    private static final String TYPE_ORIENTATION = "o";

    protected static final int INDEX_ORIENTATION = 2;

    private final int mOrientation;

    OrientationEvent(long startTime, int orientation) {
        super(startTime);
        mOrientation = orientation;
    }

    @Override
    protected String getType() {
        return TYPE_ORIENTATION;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_ORIENTATION, mOrientation);
    }
}
