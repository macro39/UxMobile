package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

import sk.brecka.uxmobile.model.ViewEnum;

/**
 * Created by matej on 4.10.2017.
 */

public abstract class ViewEvent extends TouchEvent {
    protected static final int INDEX_VIEW = 4;
    protected static final int INDEX_VIEW_TEXT = 5;
    protected static final int INDEX_VIEW_VALUE = 6;

    private final ViewEnum mViewEnum;
    private final String mViewText;
    private final String mViewValue;

    protected ViewEvent(long startTime, double x, double y, ViewEnum viewEnum, String viewText, String viewValue) {
        super(startTime, x, y);
        mViewEnum = viewEnum;
        mViewText = viewText;
        mViewValue = viewValue;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_VIEW, mViewEnum.getJsonValue())
                .put(INDEX_VIEW_TEXT, mViewText)
                .put(INDEX_VIEW_VALUE, mViewValue);
    }
}
