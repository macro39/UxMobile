package sk.brecka.uxmobile.model;

/**
 * Created by matej on 4.10.2017.
 */

public class ClickEvent extends ViewEvent {
    private static final String TAG = "ClickEvent";

    private static final String TYPE_CLICK = "c";

    public ClickEvent(long startTime, int x, int y, ViewEnum viewEnum, String viewInfo) {
        super(startTime, x, y, viewEnum, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_CLICK;
    }

}
