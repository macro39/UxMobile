package sk.uxtweak.uxmobile.study.float_widget

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat

/**
 * Created by Kamil Macek on 23. 11. 2019.
 */
class PermissionChecker(
    val activity: Activity
) : ActivityCompat.OnRequestPermissionsResultCallback {

    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    private var hasPermission = false

    fun canDrawOverlay(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                activity
            )
        ) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivityForResult(
                intent,
                CODE_DRAW_OVER_OTHER_APP_PERMISSION
            )
            hasPermission
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            hasPermission = Settings.canDrawOverlays(activity)
        }
    }

}
