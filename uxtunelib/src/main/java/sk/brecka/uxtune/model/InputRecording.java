package sk.brecka.uxtune.model;

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

    private static final String TAG_ACTIVITY_NAME = "an";
    private static final String TAG_INPUTS = "in";
    private static final String TAG_START_TIME = "st";

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

    public void addInput(float x, float y, int type) {
        mInputs.add(new Input(x, y, type, System.currentTimeMillis()));
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

            out.put(i, new JSONArray()
                    .put(Input.INDEX_X, mInputs.get(i).mX)
                    .put(Input.INDEX_Y, mInputs.get(i).mY)
                    .put(Input.INDEX_ACTION, mInputs.get(i).mAction)
                    .put(Input.INDEX_TIME, mInputs.get(i).mTime)
            );
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

    private class Input {
        public static final int INDEX_X = 0;
        public static final int INDEX_Y = 1;
        public static final int INDEX_ACTION = 2;
        public static final int INDEX_TIME = 3;

        private final float mX;
        private final float mY;
        private final int mAction;
        private final long mTime;

        private Input(float x, float y, int action, long time) {
            mX = x;
            mY = y;
            mAction = action;
            mTime = time;
        }
    }
}
