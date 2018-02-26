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
}
