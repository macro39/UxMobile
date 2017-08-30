package sk.brecka.uxtune.core;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.Window;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import sk.brecka.uxtune.model.InputRecording;
import sk.brecka.uxtune.adapter.WindowCallbackAdapter;

/**
 * Created by matej on 25.8.2017.
 */

public class InputRecorder extends BaseRecorder {

    private LinkedList<InputRecording> mInputRecordings = new LinkedList<>();

    @Override
    protected void onEveryActivityStarted(Activity activity) {
        // recordni
        mInputRecordings.addLast(new InputRecording(activity));

        //
        Window.Callback previousCallback = activity.getWindow().getCallback();

        // zabrani kopeniu
        if (previousCallback instanceof WindowCallbackAdapter) {
            return;
        }

        WindowCallbackAdapter windowCallbackAdapter = new WindowCallbackAdapter(previousCallback) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                captureInput(event);
                return super.dispatchTouchEvent(event);
            }
        };
        activity.getWindow().setCallback(windowCallbackAdapter);
    }

    private void captureInput(MotionEvent event) {
        final InputRecording currentRecording = mInputRecordings.getLast();

        // filter
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                // vhodne akcie
                break;
            case MotionEvent.ACTION_MOVE:
                // TODO: handlovat aj tuto akciu
            default:
                // nevhodne akcie
                return;
        }

        // TODO: pouzivat vlastne kody na akcie
        currentRecording.addInput(event.getX(), event.getY(), event.getAction());
    }

    public JSONArray getOutput() throws JSONException{
        JSONArray out = new JSONArray();

        for (InputRecording recording : mInputRecordings) {
            out.put(recording.toJson());
        }

        return out;
    }
}
