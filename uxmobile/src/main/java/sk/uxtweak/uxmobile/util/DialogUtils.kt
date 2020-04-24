package sk.uxtweak.uxmobile.util

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import sk.uxtweak.uxmobile.lifecycle.ForegroundActivityHolder

object DialogUtils {
    fun showDialog(title: String, message: String, action: (Activity) -> Unit = {}) {
        val activity = ForegroundActivityHolder.foregroundActivity
        if (activity != null) {
            showInfoDialog(activity, title, message, action)
        } else {
            ForegroundActivityHolder.doOnActivity {
                showInfoDialog(it, title, message, action)
            }
        }
    }

    private fun showInfoDialog(activity: Activity, title: String, message: String, action: (Activity) -> Unit) =
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> action(activity) }
            .setCancelable(false)
            .show()
}
