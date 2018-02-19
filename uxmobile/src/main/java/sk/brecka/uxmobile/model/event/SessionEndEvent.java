package sk.brecka.uxmobile.model.event;

import sk.brecka.uxmobile.model.event.Event;

/**
 * Created by matej on 19.2.2018.
 */

public class SessionEndEvent extends Event {
    public SessionEndEvent(long startTime) {
        super(startTime);
    }

    @Override
    protected String getType() {
        return TYPE_SESSION_END;
    }
}
