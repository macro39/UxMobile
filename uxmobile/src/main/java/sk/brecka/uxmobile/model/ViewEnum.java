package sk.brecka.uxmobile.model;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by matej on 4.10.2017.
 */

public enum ViewEnum {
    NONE,
    GENERIC,
    EDIT_TEXT,
    SEEK_BAR,
    BUTTON;

    public static ViewEnum fromView(final View view) {
        if (view == null) {
            return NONE;
        } else if (view instanceof EditText) {
            return EDIT_TEXT;
        } else if (view instanceof Button) {
            return BUTTON;
        } else if (view instanceof SeekBar) {
            return SEEK_BAR;
        } else {
            return GENERIC;
        }
    }

    public int getJsonValue() {
        // ekvivalentne this.ordinal(), ale nemusi byt

        switch (this) {
            case NONE:
                return 0;
            case GENERIC:
                return 1;
            case EDIT_TEXT:
                return 2;
            case SEEK_BAR:
                return 3;
            case BUTTON:
                return 4;
            default:
                return -1;
        }
    }
}
