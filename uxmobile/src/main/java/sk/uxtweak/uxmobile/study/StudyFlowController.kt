package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.float_button.FloatButtonService
import sk.uxtweak.uxmobile.study.float_button.PermissionChecker

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController (
    val context: Context
) : LifecycleObserver {

    private val TAG = this::class.java.simpleName

    private lateinit var floatButtonService: FloatButtonService
    private lateinit var permissionChecker: PermissionChecker

    init {
        ApplicationLifecycle.addObserver(this)
        configure()
    }

    private fun configure() {
        floatButtonService = FloatButtonService(context)
        Log.d(TAG, "Configured")
    }


    // TODO teraz mi tu chodia vsetky eventy
    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
        permissionChecker = PermissionChecker(activity)

        if (permissionChecker.canDrawOverlay()) {
            floatButtonService.onCreate()
        }
    }

    // kazda aktivita vratane prvej
    override fun onEveryActivityStarted(activity: Activity) {

    }

    // otvorim dalsiu,
    // TODO lifecycle activity
    override fun onEveryActivityStopped(activity: Activity) {
    }

    // minimalizacia
    override fun onLastActivityStopped(activity: Activity) {
        floatButtonService.onDestroy()
    }

    // otocenie displeja
    override fun onConfigurationChanged(configuration: Configuration) {
    }

}
