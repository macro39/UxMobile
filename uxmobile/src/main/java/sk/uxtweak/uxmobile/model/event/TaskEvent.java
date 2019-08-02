package sk.uxtweak.uxmobile.model.event;

import org.json.JSONArray;
import org.json.JSONException;

import sk.uxtweak.uxmobile.model.study.Task;

/**
 * Created by matej on 26.2.2018.
 */

public class TaskEvent extends Event {
    protected static final int INDEX_TASK_STATUS = 2;
    protected static final int INDEX_TASK_ID = 3;

    private static final String STATUS_STARTED = "s";
    private static final String STATUS_COMPLETED = "c";
    private static final String STATUS_CANCELLED = "n";
    private static final String STATUS_SKIPPED = "k";

    private final Status mStatus;
    private final long mTaskId;

    public TaskEvent(long startTime, Task task, Status status) {
        super(startTime);
        mStatus = status;
        mTaskId = task.getId();
    }

    @Override
    protected String getType() {
        return TYPE_TASK;
    }

    @Override
    public JSONArray toJson() throws JSONException {
        return super.toJson().put(INDEX_TASK_STATUS, mStatus.toString())
                .put(INDEX_TASK_ID, mTaskId);
    }

    public enum Status {
        STARTED, COMPLETED, CANCELLED, SKIPPED;

        @Override
        public String toString() {
            switch (this) {
                case STARTED:
                    return STATUS_STARTED;
                case COMPLETED:
                    return STATUS_COMPLETED;
                case CANCELLED:
                    return STATUS_CANCELLED;
                case SKIPPED:
                    return STATUS_SKIPPED;
                default:
                    return "";
            }
        }
    }
}
