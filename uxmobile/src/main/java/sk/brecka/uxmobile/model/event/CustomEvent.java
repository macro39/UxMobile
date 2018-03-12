package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by matej on 28.11.2017.
 */

public class CustomEvent extends Event {
    protected static final int INDEX_EVENT_NAME = 2;
    protected static final int INDEX_PAYLOAD = 3;

    private final String mEventName;
    private final Map<String, String> mPayload;


    public CustomEvent(long startTime, String eventName, Map<String, String> payload) {
        super(startTime);
        mEventName = eventName;
        mPayload = payload;
    }

    public CustomEvent(long startTime, String eventName) {
        this(startTime, eventName, null);
    }

    @Override
    protected String getType() {
        return TYPE_CUSTOM;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_EVENT_NAME, mEventName)
                // prazdny json ak nema payload
                .put(INDEX_PAYLOAD, mPayload != null ? new JSONObject(mPayload) : new JSONObject());
    }
}

