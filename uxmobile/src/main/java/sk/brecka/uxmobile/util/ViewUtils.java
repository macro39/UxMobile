package sk.brecka.uxmobile.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by matej on 13.11.2017.
 */

public class ViewUtils {

    public static View getTouchedView(MotionEvent motionEvent, View view) {
        return rGetTouchedView(view, motionEvent, new Rect());
    }

    private static View rGetTouchedView(View view, MotionEvent motionEvent, Rect rect) {
        // TODO: co ak sa viewy overlapuju?
        if (!view.isShown() || view.getWidth() == 0 || view.getHeight() == 0) {
            return null;
        }

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

        return null;
    }

    public static String getViewText(final View view) {
        if (view == null) {
            return "";
        } else if (view instanceof EditText) {
            // do not send edittext contents
            return "";
        } else if (view instanceof TextView) {
            return ((TextView) view).getText().toString();
        } else {
            return "";
        }

    }

    public static String getViewValue(final View view) {
        if (view == null) {
            return "";
        } else if (view instanceof EditText) {
            return "";
        } else if (view instanceof SeekBar) {
            return String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof ImageButton) {
            return "";
        } else if (view instanceof RadioButton) {
            return String.valueOf(((RadioButton) view).isChecked());
        } else if (view instanceof CheckBox) {
            return String.valueOf(((CheckBox) view).isChecked());
        } else if (view instanceof Switch) {
            return String.valueOf(((Switch) view).isChecked());
        } else {
            return "";
        }
    }

    private ViewUtils() {
    }
}
