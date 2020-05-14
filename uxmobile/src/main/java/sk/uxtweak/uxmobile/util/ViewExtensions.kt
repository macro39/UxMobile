package sk.uxtweak.uxmobile.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*

fun View.getTouchedView(motionEvent: MotionEvent): View? {
    if (!isShown || width == 0 || height == 0) {
        return null
    }
    val rect = Rect()
    getGlobalVisibleRect(rect)
    val x = motionEvent.getX(motionEvent.actionIndex).toInt()
    val y = motionEvent.getY(motionEvent.actionIndex).toInt()
    if (rect.contains(x, y)) {
        if (this is ViewGroup) {
            for (i in 0 until childCount) {
                val out = getChildAt(i).getTouchedView(motionEvent)
                if (out != null) {
                    return out
                }
            }
        } else {
            return this
        }
    }
    return null
}

val View?.viewText: String?
    get() = when (this) {
        is TextView -> text.toString()
        else -> null
    }

val View?.viewValue: String?
    get() = when (this) {
        is CompoundButton -> isChecked.toString()
        is SeekBar -> progress.toString()
        else -> null
    }

val View?.viewType: Int
    get() = when (this) {
        is EditText -> 1
        is Switch -> 2
        is RatingBar -> 3
        is CheckBox -> 4
        is RadioButton -> 5
        is ToggleButton -> 6
        is Button -> 7
        is SeekBar -> 8
        else -> 0
    }
