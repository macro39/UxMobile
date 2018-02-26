package sk.brecka.uxmobile.model.event;

/**
 * Created by matej on 26.2.2018.
 */

public class TaskEvent extends Event {
    protected static final int INDEX_TASK_STATUS = 2;

    private final Status mStatus;

    public TaskEvent(long startTime, Status status) {
        super(startTime);
        mStatus = status;
    }

    @Override
    protected String getType() {
        switch (mStatus) {
            case STARTED:
                return TYPE_TASK_START;
            case COMPLETED:
                return TYPE_TASK_COMPLETED;
            case CANCELLED:
                return TYPE_TASK_CANCELLED;
            case SKIPPED:
                return TYPE_TASK_SKIPPED;
            default:
                return "";
        }
    }

    public enum Status {
        STARTED, COMPLETED, CANCELLED, SKIPPED
    }
}
