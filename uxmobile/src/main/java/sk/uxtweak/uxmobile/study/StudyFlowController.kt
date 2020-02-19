package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import com.google.gson.GsonBuilder
import sk.uxtweak.uxmobile.core.LifecycleObserver
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.model.StudyTask
import sk.uxtweak.uxmobile.study.network.RestCommunicator
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptObserver
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragment
import sk.uxtweak.uxmobile.study.utility.SharedPreferencesChangeListener
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

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
        StudyDataHolder.tasks = listOf(
            StudyTask(
                1,
                "CREATE NEW APPOINTMENT ON 27.05.2022",
                false
            ),
            StudyTask(
                2,
                "MARK WHEN YOU HAVE BIRTHDAY",
                false
            )
        )

        restCommunicator.getStudy { res: Study? ->
            if (res != null) {
                Log.d(TAG, res.studyId.toString())

                StudyDataHolder.tasks = res.studyTasks

                for (sm: StudyMessage in res.studyMessages) {
                    Log.d(TAG, sm.type)
                }

                StudyDataHolder.study = res
            } else {
                Log.e(TAG, "NO RESPONSE, WORKING WITH DUMMY DATA")

                val gson = GsonBuilder().create()
                val dummyStudyResponse: Study =
                    gson.fromJson(dummyResponseData(), Study::class.java)

                StudyDataHolder.study = dummyStudyResponse
            }
        }

        StudyDataHolder.numberOfTasks = StudyDataHolder.tasks.size
    }

    fun dummyResponseData(): String {
        return "{\"studyId\":1,\"name\":\"STUDIA C. 1 POSTGRES\",\"studyBrandings\":{\"primaryColor\":\"#008577\",\"secondaryColor\":\"#EF9D41\",\"hibernateLazyInitializer\":{}},\"studyTasks\":[{\"taskId\":1,\"title\":\"MARK WHEN YOU HAVE BIRTHDAY\"},{\"taskId\":2,\"title\":\"CREATE NEW APPOINTMENT ON 27.05.2022\"}],\"studyMessages\":[{\"type\":\"completed\",\"title\":\"Thank you, that's all!\",\"content\":\"All done, awesome! Thanks again for your participation. Your feedback is incredibly useful in helping us understand how people interact with our app, so that we can make our application easier to use.\\r\\n\\r\\nYou may now going back to your work!\"},{\"type\":\"welcome\",\"title\":\"Welcome!\",\"content\":\"Welcome to this study, and thank you for agreeing to participate! The activity shouldn't take longer than 30 to 60 minutes to complete. Your response will help us to better understand how people behave in our app.\"},{\"type\":\"closed\",\"title\":\"Sorry, this study has concluded\",\"content\":\"Sorry, this study has concluded\\r\\nThis study has been closed and so it's no longer possible to participate. If you think that this is a mistake and you should still be able to participate, please contact the conductor of the study. We hope to see you again.\"},{\"type\":\"rejected\",\"title\":\"Thank you and hope we see you next time!\",\"content\":\"We are a little sad, but also next day is there a opportunity to change your favorite application by participating in study!\"},{\"type\":\"instructions\",\"title\":\"Instructions\",\"content\":\"<b>Here's how it works:</b><ol><li>You will be presented with a task.</li><li>After reading the task, you will be redirected to a website.</li><li>Click through the website as you naturally would in order to fulfill the task.</li><li>Once you arrive at the intended destination, click <b>Task done</b> and the task will end.</li><li>Repeat the previous steps for all the tasks to complete the RePlay study.</li></ol><em>This is not a test of your ability, there are no right or wrong answers.</em><br><b>That's it, let's get started!</b></div>\"}],\"studyQuestions\":[{\"type\":\"screening_qst\",\"atTask\":1,\"title\":\"DOTAZNIK PARTICIPANTA\",\"description\":\"Vyplne prosim dotaznik, aby sme mohli zistit, ci ste vhodnym respondentom\",\"order\":1,\"answerType\":\"input\",\"answerOptions\":\"{\\\"id\\\":123,\\\"title\\\":\\\"AKE MAS POHLAVIE?\\\",\\\"options\\\":[\\\"MUZ\\\", \\\"ZENA\\\"] }\",\"required\":true,\"randomizeOptions\":true}],\"hibernateLazyInitializer\":{}}"
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
        val selectedStudyTask: StudyTask =
            StudyDataHolder.tasks.filter { s -> s.taskId == StudyDataHolder.doingTaskWithId.toLong() }
                .single()
        selectedStudyTask.accomplished = true

        // decrement number of available tasks
        StudyDataHolder.numberOfTasks--

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
