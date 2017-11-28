package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 28.11.2017.
 */

public class CustomEvent extends Event {
    private static final String TYPE_CUSTOM = "u";

    protected static final int INDEX_EVENT_NAME = 2;

    private final String mEventName;

    public CustomEvent(long startTime, String eventName) {
        super(startTime);
        mEventName = eventName;
    }

    @Override
    protected String getType() {
        return TYPE_CUSTOM;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_EVENT_NAME, mEventName);
    }
}

