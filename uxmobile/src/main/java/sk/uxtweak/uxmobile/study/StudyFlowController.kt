package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.Task
import sk.uxtweak.uxmobile.study.network.RestCommunicator
import sk.uxtweak.uxmobile.study.utility.SharedPreferencesChangeListener
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptObserver
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragment
import java.io.IOException

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver, StudyFlowAcceptObserver {

    companion object TaskExecutionDataHolder {
        var numberOfTasks = 0
        var doingTaskWithId = -1
        lateinit var tasks: List<Task>
    }

    private val TAG = this::class.java.simpleName

    private var isInStudy = false
    private var isOnlyInstructionDisplayed = false

    // TODO should replace this true value with value from server
    private var isStudySet = true

    private var minimizedWhenInStudyFlow = false

    private val floatWidgetService: FloatWidgetService
    private val sharedPreferencesChangeListener: SharedPreferencesChangeListener
    private val restCommunicator: RestCommunicator

    init {
        ApplicationLifecycle.addObserver(this)

        sharedPreferencesChangeListener = SharedPreferencesChangeListener(context, this)

//         in case of running after crash (clear in study)
//        val shared = SharedPreferencesController(context)
//        shared.changeInStudyState(false)
//        return

        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()
        Log.d(TAG, "Configured")

        restCommunicator = RestCommunicator(context)


        // dummy data
        tasks = listOf(
            Task(
                1,
                "CREATE NEW APPOINTMENT ON 27.05.2022",
                "NAVIGATE AND FIND 27.05.2022 AND CREATE SOME APPOINTMENT",
                false
            ),
            Task(
                2,
                "MARK WHEN YOU HAVE BIRTHDAY",
                "FIND DATE OF YOUR BIRTHDAY AND ADD IT TO FAVORITES",
                false
            )
        )

        restCommunicator.getStudy { res: Study? ->
            if (res != null) {
                Log.d(TAG, res.studyId.toString())
            } else {
                Log.e(TAG, "NO RESPONSE")
            }
        }

        numberOfTasks = tasks.size
    }

    /**
     * when user admit some task to execute
     */
    override fun studyAccepted(accepted: Boolean) {
        Log.d(TAG, "STUDY ACCEPTED - $accepted")
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
        Log.d(TAG, "TASK EXECUTION ENDED")
        floatWidgetService.setVisibility(false)

        // set executed task as accomplished
        val selectedTask: Task = tasks.filter { s -> s.taskId == doingTaskWithId.toLong() }.single()
        selectedTask.accomplished = true

        // decrement number of available tasks
        numberOfTasks--

        // show fragments with flag end of task
        showStudyFlow(onlyInstructions = false, endOfTask = true)
    }

    override fun instructionClicked() {
        Log.d(TAG, "INSTRUCTION CLICKED")
        floatWidgetService.setVisibility(false)

        isOnlyInstructionDisplayed = true

        showStudyFlow(onlyInstructions = true, endOfTask = false)
    }

    private fun showStudyFlow(onlyInstructions: Boolean, endOfTask: Boolean) {
        val intent = Intent(context, StudyFlowFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)
        intent.putExtra(EXTRA_END_OF_TASK, false)
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, onlyInstructions)
        intent.putExtra(EXTRA_END_OF_TASK, endOfTask)
        context.startActivity(intent)
    }

    // ked sa spusti app
    override fun onFirstActivityStarted(activity: Activity) {
        sharedPreferencesChangeListener.addListener()

        if (minimizedWhenInStudyFlow) {
            minimizedWhenInStudyFlow = false
        } else {
            if (!isInStudy) {
                showStudyFlow(onlyInstructions = false, endOfTask = false)
            } else {
                floatWidgetService.onCreate()
                sharedPreferencesChangeListener.addListener()
            }
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
