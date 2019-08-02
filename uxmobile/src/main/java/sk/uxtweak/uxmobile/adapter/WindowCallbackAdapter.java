package sk.uxtweak.uxmobile.adapter;

import android.os.Build;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by matej on 27.8.2017.
 */

public class WindowCallbackAdapter implements Window.Callback {

    private Window.Callback mBaseCallback;

    public WindowCallbackAdapter(Window.Callback baseCallback) {
        mBaseCallback = baseCallback;
    }

    //
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchKeyEvent(event);
        }

        return false;
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchKeyShortcutEvent(event);
        }

        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchTrackballEvent(event);
        }

        return false;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchGenericMotionEvent(event);
        }

        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (mBaseCallback != null) {
            return mBaseCallback.dispatchPopulateAccessibilityEvent(event);
        }

        return false;
    }

    @Override
    public View onCreatePanelView(int featureId) {
        if (mBaseCallback != null) {
            return mBaseCallback.onCreatePanelView(featureId);
        }

        return null;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (mBaseCallback != null) {
            return mBaseCallback.onCreatePanelMenu(featureId, menu);
        }

        return false;
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (mBaseCallback != null) {
            return mBaseCallback.onPreparePanel(featureId, view, menu);
        }

        return false;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (mBaseCallback != null) {
            return mBaseCallback.onMenuOpened(featureId, menu);
        }

        return false;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (mBaseCallback != null) {
            return mBaseCallback.onMenuItemSelected(featureId, item);
        }

        return false;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
        if (mBaseCallback != null) {
            mBaseCallback.onWindowAttributesChanged(attrs);
        }
    }

    @Override
    public void onContentChanged() {
        if (mBaseCallback != null) {
            mBaseCallback.onContentChanged();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (mBaseCallback != null) {
            mBaseCallback.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onAttachedToWindow() {
        if (mBaseCallback != null) {
            mBaseCallback.onAttachedToWindow();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        if (mBaseCallback != null) {
            mBaseCallback.onDetachedFromWindow();
        }
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        if (mBaseCallback != null) {
            mBaseCallback.onPanelClosed(featureId, menu);
        }
    }

    @Override
    public boolean onSearchRequested() {
        if (mBaseCallback != null) {
            mBaseCallback.onSearchRequested();
        }

        return false;
    }

    @Override
    public boolean onSearchRequested(SearchEvent searchEvent) {
        if (mBaseCallback != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                mBaseCallback.onSearchRequested(searchEvent);
            }
        }

        return false;
    }

    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (mBaseCallback != null) {
            mBaseCallback.onWindowStartingActionMode(callback);
        }

        return null;
    }

    @Override
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        if (mBaseCallback != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                mBaseCallback.onWindowStartingActionMode(callback, type);
            }
        }

        return null;
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        if (mBaseCallback != null) {
            mBaseCallback.onActionModeStarted(mode);
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        if (mBaseCallback != null) {
            mBaseCallback.onActionModeFinished(mode);
        }
    }
}
