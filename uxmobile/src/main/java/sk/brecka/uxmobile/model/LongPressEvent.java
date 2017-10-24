package sk.brecka.uxmobile.model;

/**
 * Created by matej on 4.10.2017.
 */

public class LongPressEvent extends ViewEvent {
    private static final String TYPE_LONG_PRESS = "l";

    public LongPressEvent(int x, int y, long time, ViewEnum viewEnum, String viewInfo) {
        super(x, y, time, viewEnum, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_LONG_PRESS;
    }
}
