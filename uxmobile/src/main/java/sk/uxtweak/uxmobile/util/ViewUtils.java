package sk.uxtweak.uxmobile.util;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by matej on 13.11.2017.
 */

public class ViewUtils {

    public static View getTouchedView(MotionEvent motionEvent, View view) {
        return rGetTouchedView(view, motionEvent, new Rect());
    }

    private static View rGetTouchedView(View view, MotionEvent motionEvent, Rect rect) {
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
        } else if (view instanceof SeekBar) {
            return String.valueOf(((SeekBar) view).getProgress());
        } else if (view instanceof CompoundButton) {
            return String.valueOf(((CompoundButton) view).isChecked());
        } else {
            return "";
        }
    }

    private ViewUtils() {
    }
}
