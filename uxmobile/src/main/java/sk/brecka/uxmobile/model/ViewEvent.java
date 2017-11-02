package sk.brecka.uxmobile.model;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by matej on 4.10.2017.
 */

public abstract class ViewEvent extends TouchEvent {
    private static final String TAG = "ViewEvent";

    protected static final int INDEX_VIEW = 4;
    protected static final int INDEX_VIEW_INFO = 5;

    protected ViewEnum mViewEnum;
    protected String mViewInfo;

    public ViewEvent(long startTime, int x, int y, ViewEnum viewEnum, String viewInfo) {
        super(startTime, x, y);
        mViewEnum = viewEnum;
        mViewInfo = viewInfo;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson()
                .put(INDEX_VIEW, mViewEnum.getJsonValue())
                .put(INDEX_VIEW_INFO, mViewInfo);
    }
}
