package sk.uxtweak.uxmobile.model.event;

import sk.uxtweak.uxmobile.model.ViewEnum;

/**
 * Created by matej on 4.10.2017.
 */

public class LongPressEvent extends ViewEvent {

    public LongPressEvent(long startTime, double x, double y , ViewEnum viewEnum, String viewText, String viewInfo) {
        super(startTime, x, y, viewEnum, viewText, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_LONG_PRESS;
    }
}
