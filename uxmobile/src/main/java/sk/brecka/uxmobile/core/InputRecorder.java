package sk.brecka.uxmobile.core;

import android.app.Activity;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;

import sk.brecka.uxmobile.adapter.WindowCallbackAdapter;
import sk.brecka.uxmobile.model.ClickInput;
import sk.brecka.uxmobile.model.Input;
import sk.brecka.uxmobile.model.InputRecording;
import sk.brecka.uxmobile.model.LongPressInput;
import sk.brecka.uxmobile.model.ScrollInput;
import sk.brecka.uxmobile.model.ViewEnum;


/**
 * Created by matej on 25.8.2017.
 */

public class InputRecorder extends BaseRecorder implements GestureDetector.OnGestureListener {

    // TODO: flushovat recordingy po odoslani
    private LinkedList<InputRecording> mInputRecordings = new LinkedList<>();
    private GestureDetector mGestureDetector;

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

        mGestureDetector = new GestureDetector(activity, this);

        activity.getWindow().setCallback(new WindowCallbackAdapter(previousCallback) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return super.dispatchTouchEvent(event);
            }
        });

    }

    @Override
    protected void onLastActivityStopped(Activity activity) {
        System.out.println("Recordings:");
        for (InputRecording inputRecording : mInputRecordings) {
            try {
                System.out.println(inputRecording.toJson());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // gesture listener metody
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        recordInput(new ClickInput(e.getX(), e.getY(), ViewEnum.fromView(touchedView), getViewInfo(touchedView)));
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        recordInput(new ScrollInput(e2.getX(), e2.getY(), distanceX, distanceY));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final View touchedView = getTouchedView(e);
        recordInput(new LongPressInput(e.getX(), e.getY(), ViewEnum.fromView(touchedView), getViewInfo(touchedView)));
    }

    //
    @Override
    public boolean onDown(MotionEvent e) {
        // intentionally blank
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // intentionally blank
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // intentionally blank
        return false;
    }

    // TODO: presunut do samostatnej classy
    private View getTouchedView(MotionEvent motionEvent) {
        return rGetTouchedView(mCurrentActivity.getWindow().getDecorView().getRootView(), motionEvent, new Rect());
    }

    private View rGetTouchedView(View view, MotionEvent motionEvent, Rect rect) {
        // TODO: co ak sa viewy overlapuju?
        if (!view.isShown() || view.getWidth() == 0 || view.getHeight() == 0) {
            return null;
        }

        // TODO: otestovat

        view.getGlobalVisibleRect(rect);

        final int x = (int) motionEvent.getX(motionEvent.getActionIndex());
        final int y = (int) motionEvent.getY(motionEvent.getActionIndex());

        if (rect.contains(x, y)) {
            if (view instanceof ViewGroup) {
                //
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    final View out = rGetTouchedView(((ViewGroup) view).getChildAt(i), motionEvent, rect);
                    if (out != null) {
                        return out;
                    }
                }
            } else {
                return view;
            }
        }
//        if (view instanceof ViewGroup) {
//            // TODO: ak to neintersectuje s viewgroupou, tak nemoze ani s detmi
//            // najdi medzi detmi
//
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                final View out = rGetTouchedView(((ViewGroup) view).getChildAt(i), motionEvent);
//                if (out != null) {
//                    return out;
//                }
//            }
//
//        } else {
//
//
//            if (rect.contains(x, y)) {
////                System.out.println("Returning " + view);
//                return view;
//            }
//        }
        return null;
    }

    private String getViewInfo(final View view) {
        if (view == null) {
            return "";
        } else if (view instanceof EditText) {
            return "";
        } else if (view instanceof Button) {
            return ((Button) view).getText().toString();
        } else if (view instanceof SeekBar) {
            return String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof ImageButton) {
            return "";
        } else {
            return "";
        }
    }

//    private void printInteraction(MotionEvent event) {
//        final View view = getTouchedView(event);
//
//        if (view == null) {
//        } else if (view instanceof Button) {
//            System.out.println("Clicked button \"" + ((Button) view).getText() + "\"");
//        } else if (view instanceof EditText) {
//            System.out.println("Clicked edittext");
//        } else if (view instanceof SeekBar) {
//            System.out.println("Set SeekBar to " + ((SeekBar) view).getProgress());
//        } else {
//            System.out.println("Interacted with view " + view.getClass().getSimpleName());
//        }
//    }

    private void recordInput(Input input) {
        mInputRecordings.getLast().addInput(input);
    }

    public JSONArray getOutput() throws JSONException {
        JSONArray out = new JSONArray();

        for (InputRecording recording : mInputRecordings) {
            out.put(recording.toJson());
        }

        return out;
    }

}
