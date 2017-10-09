package sk.brecka.uxmobile.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 4.10.2017.
 */

public class LongPressInput extends ViewInput {
    private static final String TYPE_LONG_PRESS = "l";
    public LongPressInput(float x, float y, ViewEnum viewEnum, String viewInfo) {
        super(x, y, viewEnum, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_LONG_PRESS;
    }
}
