package sk.brecka.uxmobile.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 4.10.2017.
 */

public class ClickInput extends ViewInput {
    private static final String TYPE_CLICK = "c";

    public ClickInput(float x, float y, ViewEnum viewEnum, String viewInfo) {
        super(x, y, viewEnum, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_CLICK;
    }

}
