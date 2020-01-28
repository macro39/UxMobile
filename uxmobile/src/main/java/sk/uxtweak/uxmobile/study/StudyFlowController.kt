package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
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

    companion object TaskExecutionDataHolder {
        var numberOfTasks = 0
        var doingTaskWithId = -1
    }

    private val TAG = this::class.java.simpleName

    private var isInStudy = false
    private var isOnlyInstructionDisplayed = false

    // TODO should replace this true value with value from server
    private var isStudySet = true

    private var minimizedWhenInStudyFlow = false

    private var numberOfTasks = 2

    private lateinit var floatWidgetService: FloatWidgetService
    private lateinit var sharedPreferencesChangeListener: SharedPreferencesChangeListener

    init {
        ApplicationLifecycle.addObserver(this)
        configure()

        // dummy
        TaskExecutionDataHolder.numberOfTasks = numberOfTasks
    }

    private fun configure() {
        sharedPreferencesChangeListener = SharedPreferencesChangeListener(context, this)

//        val shared = SharedPreferencesController(context)
//        shared.changeInStudyState(false)

        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()
        Log.d(TAG, "Configured")
    }

    /**
     * when user admit some task to execute
     */
    override fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            Log.d(TAG, "Accepted taking a part in study")
            // for first time
            if (!isInStudy) {
                isInStudy = true
                floatWidgetService.onCreate()
                floatWidgetService.setVisibility(true)
            } else {
                floatWidgetService.setVisibility(true)
            }
        } else {
            isInStudy = false
            floatWidgetService.onDestroy()
            // TODO when hit later, shouldn't remove listener
            sharedPreferencesChangeListener.removeListener()
            sharedPreferencesChangeListener.changeInStudyState(false)
            Log.d(TAG, "Rejected taking a part in study")
        }
    }

    override fun taskExecutionEnded() {
        floatWidgetService.setVisibility(false)

        // decrement number of available tasks
        numberOfTasks--
        TaskExecutionDataHolder.numberOfTasks = numberOfTasks

        // show fragments with flag end of task
        val intent = Intent(context, StudyFlowFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, false)
        intent.putExtra(EXTRA_END_OF_TASK, true)
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        context.startActivity(intent)
    }

    override fun instructionClicked() {
        floatWidgetService.setVisibility(false)

        isOnlyInstructionDisplayed = true

        val intent = Intent(context, StudyFlowFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)
        intent.putExtra(EXTRA_END_OF_TASK, false)
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        context.startActivity(intent)
    }

    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
        // TODO should change this, when leaving app when in study flow - it will crash
//        if (minimizedWhenInStudyFlow) {
//            minimizedWhenInStudyFlow = false
//            return
//        }

        sharedPreferencesChangeListener.addListener()

        if (!isInStudy) {
            val intent = Intent(context, StudyFlowFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, false)
            intent.putExtra(EXTRA_END_OF_TASK, false)
            intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
            context.startActivity(intent)
        } else {
            floatWidgetService.onCreate()
            sharedPreferencesChangeListener.addListener()
        }
    }

    // kazda aktivita vratane prvej
    override fun onEveryActivityStarted(activity: Activity) {
    }

    // otvorim dalsiu
    // TODO lifecycle context
    override fun onEveryActivityStopped(activity: Activity) {
        // detect if stopped context is study flow
        if (StudyFlowFragment::class.qualifiedName.equals(activity.localClassName)) {
            if (!isOnlyInstructionDisplayed) {
                // make float widget collapsed - another task was accept
                floatWidgetService.changeFloatButtonState(false)
            } else {
                isOnlyInstructionDisplayed = false
            }
            if (isInStudy) {
                floatWidgetService.setVisibility(true)
            }
        }
    }

    // minimalizacia
    override fun onLastActivityStopped(activity: Activity) {
        // if user minimize app when study flow is on
        if (StudyFlowFragment::class.qualifiedName.equals(activity.localClassName)) {
            minimizedWhenInStudyFlow = true
            return
        }

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
