package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.study_flow.InstructionActivity
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptedObserver
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragment

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver, StudyFlowAcceptedObserver {

    private val TAG = this::class.java.simpleName

    private var isInStudy = false

    private lateinit var floatWidgetService: FloatWidgetService
    private lateinit var sharedPreferenceChangeListener: SharedPreferenceChangeListener

    init {
        ApplicationLifecycle.addObserver(this)
        configure()
    }

    private fun configure() {
        isInStudy = false
        sharedPreferenceChangeListener = SharedPreferenceChangeListener(context, this)

        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()
        Log.d(TAG, "Configured")
    }

    /**
     * when user hit yes/no button in
     */
    override fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            Log.d(TAG, "Accepted taking a part in study")
//            if (permissionChecker.canDrawOverlay()) {
//                floatWidgetService.onCreate()
//            }

            if (!isInStudy) {
                isInStudy = true
                floatWidgetService.onCreate()
            }
        } else {
            isInStudy = false
            Log.d(TAG, "Rejected taking a part in study")
        }
    }

    override fun studyStateChanged(studyInProgress: Boolean) {
        isInStudy = studyInProgress

        //TODO disable video recording and remove float widget
        if (!isInStudy) {
            floatWidgetService.onDestroy()
            sharedPreferenceChangeListener.changeInStudyState(false)
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
//        permissionChecker = PermissionChecker(context)

        // when starting as context
//        val intent = Intent(context, GlobalMessageActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)
        sharedPreferenceChangeListener.addListener()

        if (!isInStudy) {
            // when starting as fragment
            val intent = Intent(context, StudyFlowFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            floatWidgetService.onCreate()
        }

//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        context.startActivity(intent)

//        if (permissionChecker.canDrawOverlay()) {
//            floatWidgetService.onCreate()
//        }
    }

    // kazda aktivita vratane prvej
    override fun onEveryActivityStarted(activity: Activity) {
    }

    // otvorim dalsiu,
    // TODO lifecycle context
    override fun onEveryActivityStopped(activity: Activity) {
        // detect if stopped context is instruction context
        if (InstructionActivity::class.qualifiedName.equals(activity.localClassName)) {
            floatWidgetService.setVisibility(true)
        }
    }

    // minimalizacia
    override fun onLastActivityStopped(activity: Activity) {
        floatWidgetService.onDestroy()
        sharedPreferenceChangeListener.removeListener()

        // only for testing - remove
//        val controller = SharedPreferencesController(context)
//        controller.changeInStudyState(false)
//        isInStudy = false
    }

    // otocenie displeja
    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
