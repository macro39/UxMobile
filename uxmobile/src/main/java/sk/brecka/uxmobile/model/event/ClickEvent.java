package sk.brecka.uxmobile.model.event;

import sk.brecka.uxmobile.model.ViewEnum;

/**
 * Created by matej on 4.10.2017.
 */

public class ClickEvent extends ViewEvent {
    private static final String TYPE_CLICK = "c";

    ClickEvent(long startTime, int x, int y, ViewEnum viewEnum, String viewInfo) {
        super(startTime, x, y, viewEnum, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_CLICK;
    }

}
