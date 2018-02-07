package sk.brecka.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;

import sk.brecka.uxmobile.model.event.Event;

/**
 * Created by matej on 7.2.2018.
 */

public class ExceptionEvent extends Event {
    protected static final int INDEX_STACKTRACE = 2;

    private final Throwable mThrowable;

    public ExceptionEvent(long startTime, Throwable throwable) {
        super(startTime);
        mThrowable = throwable;
    }

    @Override
    protected String getType() {
        return TYPE_EXCEPTION;
    }

    @Override
    public JSONArray toJson() throws JSONException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        mThrowable.printStackTrace(pw);
        String stackTrace = sw.toString();

        return super.toJson()
                .put(INDEX_STACKTRACE, stackTrace);
    }
}
