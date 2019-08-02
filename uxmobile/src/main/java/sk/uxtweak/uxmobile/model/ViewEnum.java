package sk.uxtweak.uxmobile.model;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

class GenericView {}

public enum ViewEnum {
    NONE(0, null),
    GENERIC(1, GenericView.class),
    EDIT_TEXT(2, EditText.class),
    SEEK_BAR(3, SeekBar.class),
    BUTTON(4, Button.class),

    SWITCH(5, Switch.class),
    CHECK_BOX(6, CheckBox.class),
    RADIO_BUTTON(7, RadioButton.class),
    TOGGLE_BUTTON(8, ToggleButton.class),
    RATING_BAR(9, RatingBar.class);

    private int jsonValue;
    private Class<?> clazz;

    ViewEnum(int jsonValue, Class<?> clazz) {
        this.jsonValue = jsonValue;
        this.clazz = clazz;
    }

    public static ViewEnum fromView(final View view) {
        for (ViewEnum viewEnum : ViewEnum.values()) {
            if (viewEnum.clazz.isAssignableFrom(view.getClass())) {
                return viewEnum;
            }
        }
        return GENERIC;
    }

    public int getJsonValue() {
        return jsonValue;
    }
}
