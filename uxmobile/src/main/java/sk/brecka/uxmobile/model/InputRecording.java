package sk.brecka.uxmobile.model;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matej on 30.8.2017.
 */

public class InputRecording {

    private static final String TAG_ACTIVITY_NAME = "activity_name";
    private static final String TAG_INPUTS = "inputs";
    private static final String TAG_START_TIME = "start_time";

    private final String mActivityName;
    private final long mStart;
    private final List<Input> mInputs = new ArrayList<>();

    public InputRecording(String activityName, long start) {
        mActivityName = activityName;
        mStart = start;
    }

    public InputRecording(Activity activity) {
        this(activity.getLocalClassName(), System.currentTimeMillis());
    }

    public void addClickInput(int x, int y, ViewEnum viewEnum, String viewString) {
        mInputs.add(new ClickInput(x, y, mStart, viewEnum, viewString));
    }

    public void addLongPressInput(int x, int y, ViewEnum viewEnum, String viewString) {
        mInputs.add(new LongPressInput(x, y, mStart, viewEnum, viewString));
    }

    public void addScrollinput(int x, int y, int distanceX, int distanceY) {
        mInputs.add(new ScrollInput(x, y, mStart, distanceX, distanceY));
    }

    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(TAG_ACTIVITY_NAME, mActivityName)
                .put(TAG_START_TIME, mStart)
                .put(TAG_INPUTS, inputsToJson());
    }

    private JSONArray inputsToJson() throws JSONException {
        JSONArray out = new JSONArray();

        for (int i = 0; i < mInputs.size(); i++) {
            out.put(i, mInputs.get(i).toJson());
        }

        return out;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InputRecording that = (InputRecording) o;

        if (mStart != that.mStart) return false;
        return mActivityName != null ? mActivityName.equals(that.mActivityName) : that.mActivityName == null;

    }

    @Override
    public int hashCode() {
        int result = mActivityName != null ? mActivityName.hashCode() : 0;
        result = 31 * result + (int) (mStart ^ (mStart >>> 32));
        return result;
    }
}
