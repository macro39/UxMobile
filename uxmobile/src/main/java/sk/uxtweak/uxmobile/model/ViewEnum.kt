package sk.uxtweak.uxmobile.model

import android.view.View
import android.widget.*

interface NoneView
interface GenericView

@Suppress("unused")
enum class ViewEnum(val jsonValue: Int, val clazz: Class<*>) {
    NONE(0, NoneView::class.java),
    GENERIC(1, GenericView::class.java),
    EDIT_TEXT(2, EditText::class.java),
    SEEK_BAR(3, SeekBar::class.java),
    BUTTON(4, Button::class.java),

    SWITCH(5, Switch::class.java),
    CHECK_BOX(6, CheckBox::class.java),
    RADIO_BUTTON(7, RadioButton::class.java),
    TOGGLE_BUTTON(8, ToggleButton::class.java),
    RATING_BAR(9, RatingBar::class.java);

    companion object {
        @JvmStatic
        fun fromView(view: View?): ViewEnum {
            if (view == null) return NONE
            for (viewEnum in values()) {
                if (viewEnum.clazz.isAssignableFrom(view::class.java)) {
                    return viewEnum
                }
            }
            return GENERIC
        }
    }
}
