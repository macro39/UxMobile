package sk.brecka.uxmobile.model.event;

import sk.brecka.uxmobile.model.ViewEnum;

/**
 * Created by matej on 4.10.2017.
 */

public class ClickEvent extends ViewEvent {
    private static final String TYPE_CLICK = "c";

    public ClickEvent(long startTime, double x, double y, ViewEnum viewEnum, String viewText, String viewInfo) {
        super(startTime, x, y, viewEnum, viewText, viewInfo);
    }

    @Override
    protected String getType() {
        return TYPE_CLICK;
    }

}
