package sk.uxtweak.uxmobile.adapter

import android.os.Build
import android.view.*
import android.view.accessibility.AccessibilityEvent

open class WindowCallbackAdapter(val baseCallback: Window.Callback? = null) : Window.Callback {
    override fun dispatchKeyEvent(event: KeyEvent?) = baseCallback?.dispatchKeyEvent(event) ?: false

    override fun dispatchKeyShortcutEvent(event: KeyEvent?) =
        baseCallback?.dispatchKeyShortcutEvent(event) ?: false

    override fun dispatchTouchEvent(event: MotionEvent) =
        baseCallback?.dispatchTouchEvent(event) ?: false

    override fun dispatchTrackballEvent(event: MotionEvent?) =
        baseCallback?.dispatchTrackballEvent(event) ?: false

    override fun dispatchGenericMotionEvent(event: MotionEvent?) =
        baseCallback?.dispatchGenericMotionEvent(event) ?: false

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?) =
        baseCallback?.dispatchPopulateAccessibilityEvent(event) ?: false

    override fun onCreatePanelView(featureId: Int) = baseCallback?.onCreatePanelView(featureId)

    override fun onCreatePanelMenu(featureId: Int, menu: Menu?) =
        baseCallback?.onCreatePanelMenu(featureId, menu) ?: false

    override fun onPreparePanel(featureId: Int, view: View?, menu: Menu?) =
        baseCallback?.onPreparePanel(featureId, view, menu) ?: false

    override fun onMenuOpened(featureId: Int, menu: Menu?) =
        baseCallback?.onMenuOpened(featureId, menu) ?: false

    override fun onMenuItemSelected(featureId: Int, item: MenuItem?) =
        baseCallback?.onMenuItemSelected(featureId, item) ?: false

    override fun onWindowAttributesChanged(attrs: WindowManager.LayoutParams?) {
        baseCallback?.onWindowAttributesChanged(attrs)
    }

    override fun onContentChanged() {
        baseCallback?.onContentChanged()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        baseCallback?.onWindowFocusChanged(hasFocus)
    }

    override fun onAttachedToWindow() {
        baseCallback?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        baseCallback?.onDetachedFromWindow()
    }

    override fun onPanelClosed(featureId: Int, menu: Menu?) {
        baseCallback?.onPanelClosed(featureId, menu)
    }

    override fun onSearchRequested() = baseCallback?.onSearchRequested() ?: false

    override fun onSearchRequested(searchEvent: SearchEvent?) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            baseCallback?.onSearchRequested(searchEvent) ?: false
        } else false

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?) =
        baseCallback?.onWindowStartingActionMode(callback)

    override fun onWindowStartingActionMode(
        callback: ActionMode.Callback?,
        type: Int
    ) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        baseCallback?.onWindowStartingActionMode(callback, type)
    } else null

    override fun onActionModeStarted(mode: ActionMode?) {
        baseCallback?.onActionModeStarted(mode)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        baseCallback?.onActionModeFinished(mode)
    }
}
