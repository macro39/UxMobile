package sk.brecka.uxmobile.model;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

/**
 * Created by matej on 4.10.2017.
 */

public enum ViewEnum {
    NONE,
    GENERIC,
    EDIT_TEXT,
    SEEK_BAR,
    BUTTON,

    SWITCH,
    CHECK_BOX,
    RADIO_BUTTON,
    TOGGLE_BUTTON,
    RATING_BAR;

    public static ViewEnum fromView(final View view) {
        if (view == null) {
            return NONE;
        } else if (view instanceof EditText) {
            return EDIT_TEXT;
        } else if (view instanceof Switch) {
            return SWITCH;
        } else if (view instanceof RatingBar) {
            return RATING_BAR;
        } else if (view instanceof CheckBox) {
            return CHECK_BOX;
        } else if (view instanceof RadioButton) {
            return RADIO_BUTTON;
        } else if (view instanceof ToggleButton) {
            return TOGGLE_BUTTON;
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
            case SWITCH:
                return 5;
            case CHECK_BOX:
                return 6;
            case RADIO_BUTTON:
                return 7;
            case TOGGLE_BUTTON:
                return 8;
            case RATING_BAR:
                return 9;
            default:
                return -1;
        }
    }
}
