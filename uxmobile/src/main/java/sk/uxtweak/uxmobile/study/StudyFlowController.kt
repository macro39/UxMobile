package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.flow_activities.GlobalMessageActivity
import sk.uxtweak.uxmobile.study.flow_activities.InstructionActivity

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver {
    private val TAG = this::class.java.simpleName

    private var isInStudy = false

    private lateinit var floatWidgetService: FloatWidgetService
    private lateinit var permissionChecker: PermissionChecker

    init {
        ApplicationLifecycle.addObserver(this)
        configure()
    }

    private fun configure() {
        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()
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
        floatWidgetService.setVisibility(!instructionClicked)

        val intent = Intent(context, InstructionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
//        permissionChecker = PermissionChecker(activity)

        val intent = Intent(context, GlobalMessageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

//        if (permissionChecker.canDrawOverlay()) {
//            floatWidgetService.onCreate()
//        }
    }

    // kazda aktivita vratane prvej
    override fun onEveryActivityStarted(activity: Activity) {
    }

    // otvorim dalsiu,
    // TODO lifecycle activity
    override fun onEveryActivityStopped(activity: Activity) {
        // detect if stopped activity is instruction activity
        if (InstructionActivity::class.qualifiedName.equals(activity.localClassName)) {
            floatWidgetService.setVisibility(true)
        }
        if (GlobalMessageActivity::class.qualifiedName.equals(activity.localClassName)) {
            permissionChecker = PermissionChecker(activity)

            if (permissionChecker.canDrawOverlay()) {
            floatWidgetService.onCreate()
            }
        }
    }

    // minimalizacia
    override fun onLastActivityStopped(activity: Activity) {
        floatWidgetService.onDestroy()
    }

    // otocenie displeja
    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
