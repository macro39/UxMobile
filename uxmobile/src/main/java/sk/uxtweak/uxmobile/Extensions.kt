package sk.uxtweak.uxmobile

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

fun ViewGroup.findViewAt(x: Int, y: Int): View? {
    for (child in children) {
        if (child is ViewGroup) {
            val view = child.findViewAt(x, y)
            if (view != null && view.isShown) {
                return view
            }
        } else {
            val bounds = Rect()
            getHitRect(bounds)
            if (bounds.contains(x, y)) return this
        }
    }
    return null
}
