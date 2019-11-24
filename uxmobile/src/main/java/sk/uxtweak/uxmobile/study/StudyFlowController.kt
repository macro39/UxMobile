package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver {

    private var isInStudy = false

    private val TAG = this::class.java.simpleName

    private lateinit var floatWidgetService: FloatWidgetService
    private lateinit var permissionChecker: PermissionChecker

    init {
        ApplicationLifecycle.addObserver(this)
        configure()
    }

    private fun configure() {
        floatWidgetService = FloatWidgetService(context, this)
        Log.d(TAG, "Configured")
    }

    override fun studyStateChanged(studyInProgress: Boolean) {
        isInStudy = studyInProgress

        //TODO disable video recording and remove float widget
        if (!isInStudy) {
            floatWidgetService.onDestroy()
        }
    }

    override fun instructionClicked(instructionClicked: Boolean) {
    }

    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
        permissionChecker = PermissionChecker(activity)

        if (permissionChecker.canDrawOverlay()) {
            floatWidgetService.onCreate()
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
        floatWidgetService.onDestroy()
    }

    // otocenie displeja
    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
