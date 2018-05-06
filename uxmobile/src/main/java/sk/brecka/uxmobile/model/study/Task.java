package sk.brecka.uxmobile.model.study;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matej on 26.2.2018.
 */

public class Task {
    private final long mId;
    private final String mTitle;
    private final String mMessage;

    public Task(long id, String title, String message) {
        mId = id;
        mTitle = title;
        mMessage = message;
    }

    public static Task fromJson(JSONObject jsonObject) throws JSONException {
        return new Task(jsonObject.getLong("task_id"), jsonObject.getString("title"), jsonObject.getString("message"));
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return mId == task.mId &&
                mTitle.equals(task.mTitle) &&
                mMessage.equals(task.mMessage);
    }

    @Override
    public int hashCode() {

        long result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + mMessage != null ? mMessage.hashCode() : 0;
        result = 31 * result + (mId ^ (mId >>> 32));
        return (int) result;
    }
}
