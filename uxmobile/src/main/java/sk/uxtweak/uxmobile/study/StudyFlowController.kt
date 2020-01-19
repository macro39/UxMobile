package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.shared_preferences_utility.SharedPreferencesChangeListener
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptObserver
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragment

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver, StudyFlowAcceptObserver {

    private val TAG = this::class.java.simpleName

    private var isInStudy = false
    private var isOnlyInstructionDisplayed = false

    // TODO should replace this true value with value from server
    private var isStudySet = true

    private lateinit var floatWidgetService: FloatWidgetService
    private lateinit var sharedPreferencesChangeListener: SharedPreferencesChangeListener

    init {
        ApplicationLifecycle.addObserver(this)
        configure()
    }

    private fun configure() {
        isInStudy = false
        sharedPreferencesChangeListener = SharedPreferencesChangeListener(context, this)

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

        //TODO disable video recording, remove float widget and delete listener
        if (!isInStudy) {
            floatWidgetService.onDestroy()
            sharedPreferencesChangeListener.removeListener()
            sharedPreferencesChangeListener.changeInStudyState(false)
        }
    }

    override fun instructionClicked(instructionClicked: Boolean) {
        floatWidgetService.setVisibility(!instructionClicked)

        isOnlyInstructionDisplayed = true

        val intent = Intent(context, StudyFlowFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        context.startActivity(intent)
    }

    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
        sharedPreferencesChangeListener.addListener()

        if (!isInStudy) {
            val intent = Intent(context, StudyFlowFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, false)
            intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
            context.startActivity(intent)
        } else {
            floatWidgetService.onCreate()
        }
    }

    // kazda aktivita vratane prvej
    override fun onEveryActivityStarted(activity: Activity) {
    }

    // otvorim dalsiu,
    // TODO lifecycle context
    override fun onEveryActivityStopped(activity: Activity) {
        // detect if stopped context is instruction context
        // TODO change this visibility set
        if (StudyFlowFragment::class.qualifiedName.equals(activity.localClassName) && isOnlyInstructionDisplayed) {
            floatWidgetService.setVisibility(true)
        }
    }

    // minimalizacia
    override fun onLastActivityStopped(activity: Activity) {
        floatWidgetService.onDestroy()
        sharedPreferencesChangeListener.removeListener()

        // only for testing - remove
//        val controller = SharedPreferencesController(context)
//        controller.changeInStudyState(false)
//        isInStudy = false
    }

    // otocenie displeja
    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
