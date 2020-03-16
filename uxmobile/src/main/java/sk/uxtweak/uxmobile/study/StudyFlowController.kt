package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import sk.uxtweak.uxmobile.study.model.QuestionnaireRules
import sk.uxtweak.uxmobile.study.model.StudyTask
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context
) : LifecycleObserver, FloatWidgetClickObserver {

    private val TAG = this::class.java.simpleName

    private var isInStudy = false
    private var studyEnded = false
    private var minimizedWhenInStudyFlow = false

    // TODO should replace this true value with value from server
    private var isStudySet = true

    private val floatWidgetService: FloatWidgetService

    private lateinit var broadcastReceiver: BroadcastReceiver

    init {
        ApplicationLifecycle.addObserver(this)

        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()
        setupBroadcastReceiver()
        Log.d(TAG, "Configured")

        setDummyQuestionnaireRules()
    }

    private fun setDummyQuestionnaireRules() {
        val questionnaireRules = "{\n" +
            "    \"title\": \"SCREENING QUESTIONNAIRE\",\n" +
            "    \"description\": \"Please answers this question\",\n" +
            "    \"rules\": [\n" +
            "        {\n" +
            "            \"id\": \"1\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"What's your gender?\",\n" +
            "            \"answer_type\": \"radiobtn\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false,\n" +
            "            \"question_options\": [\n" +
            "                \"man\",\n" +
            "                \"women\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"2\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"How old are you?\",\n" +
            "            \"answer_type\": \"dropdown\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false,\n" +
            "            \"question_options\": [\n" +
            "                \"<18\",\n" +
            "                \">18\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"3\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"Where are you from?\",\n" +
            "            \"answer_type\": \"input\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"4\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"What's your favourite song?\",\n" +
            "            \"answer_type\": \"textarea\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"5\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"Are you only child?\",\n" +
            "            \"answer_type\": \"checkbox\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false,\n" +
            "            \"question_options\": [\n" +
            "                \"yes\",\n" +
            "                \"no\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"6\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"Do you agree - Cancer is the worst disease?\",\n" +
            "            \"answer_type\": \"5_point_linker_scale\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"7\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"Do you agree - BMW is the best car producer?\",\n" +
            "            \"answer_type\": \"7_point_linker_scale\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"8\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"Do you agree - 70% of people will have corona in one year?\",\n" +
            "            \"answer_type\": \"net_promoter_score\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false\n" +
            "        }\n" +
            "    ]\n" +
            "}"

        val gson = GsonBuilder().create()
        val dummyQuestionnaireRules: QuestionnaireRules =
            gson.fromJson(questionnaireRules, QuestionnaireRules::class.java)

        StudyDataHolder.questionnaireRules = dummyQuestionnaireRules
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.d(TAG, "BROADCAST RECEIVED")

                val action = intent?.action

                if (Constants.RECEIVER_IN_STUDY == action) {
                    if (intent.getBooleanExtra(
                            Constants.RECEIVER_STUDY_RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED,
                            true
                        )
                    ) {
                        floatWidgetService.changeFloatButtonState(true)
                        floatWidgetService.setVisibility(true)
                        return
                    }
                    if (intent.getBooleanExtra(Constants.RECEIVER_STUDY_ENDED, true)) {
                        // user ended study
                        studyEnded()
                        return
                    }
                    if (intent.getBooleanExtra(Constants.RECEIVER_IN_STUDY, true)) {
                        studyAccepted(true)
                    } else {
                        studyAccepted(false)
                    }
                }
            }
        }
    }

    private fun registerBroadcastReciever(register: Boolean) {
        if (register) {
            context.registerReceiver(broadcastReceiver, IntentFilter(Constants.RECEIVER_IN_STUDY))
        } else {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    /**
     * when user admit some task to execute
     */
    fun studyAccepted(accepted: Boolean) {
        Log.d(TAG, "STUDY ACCEPTED - $accepted")
        if (accepted) {
            Log.d(TAG, "Accepted taking a part in study")
            // for first time
            if (!isInStudy) {
                isInStudy = true
                floatWidgetService.onCreate()
                floatWidgetService.setVisibility(true)
            } else {
                // another task accepted
                floatWidgetService.setVisibility(true)
                floatWidgetService.changeFloatButtonState(false)
            }
        } else {
            Log.d(TAG, "Rejected taking a part in study")
            studyEnded()
        }
    }

    private fun studyEnded() {
        isInStudy = false
        studyEnded = true
        floatWidgetService.onDestroy()
        registerBroadcastReciever(false)
        Log.d(TAG, "STUDY ENDED")
    }

    override fun taskExecutionEnded(successfully: Boolean) {
        Log.d(TAG, "TASK EXECUTION ENDED")
        floatWidgetService.setVisibility(false)

        // set executed task as accomplished
        val selectedStudyTask: StudyTask =
            StudyDataHolder.tasks.single { s -> s.name == StudyDataHolder.doingTaskWithName }
        selectedStudyTask.accomplished = true
        selectedStudyTask.endedSuccessful = successfully

        // decrement number of available tasks
        StudyDataHolder.numberOfTasks--

        // show fragments with flag end of task
        showStudyFlow(onlyInstructions = false, endOfTask = true)
    }

    override fun instructionClicked() {
        Log.d(TAG, "INSTRUCTIONS CLICKED")
        floatWidgetService.setVisibility(false)

        showStudyFlow(onlyInstructions = true, endOfTask = false)
    }

    override fun onClick() {
        if (isInStudy) {
            floatWidgetService.changeFloatButtonState(false)
        }
    }

    private fun showStudyFlow(onlyInstructions: Boolean, endOfTask: Boolean) {
        registerBroadcastReciever(true)
        val intent = Intent(context, StudyFlowFragmentManager::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, onlyInstructions)
        intent.putExtra(EXTRA_END_OF_TASK, endOfTask)
        context.startActivity(intent)
    }

    override fun onFirstActivityStarted(activity: Activity) {
        if (!studyEnded) {
            registerBroadcastReciever(true)

            if (minimizedWhenInStudyFlow) {
                minimizedWhenInStudyFlow = false
            } else {
                if (!isInStudy) {
                    showStudyFlow(onlyInstructions = false, endOfTask = false)
                } else {
                    floatWidgetService.onCreate()
                }
            }
        }
    }

    override fun onEveryActivityStarted(activity: Activity) {
    }

    override fun onEveryActivityStopped(activity: Activity) {
    }

    override fun onLastActivityStopped(activity: Activity) {
        // if user minimize app when study flow is on
        if (StudyFlowFragmentManager::class.qualifiedName.equals(activity.localClassName)) {
            minimizedWhenInStudyFlow = true
            return
        }

        if (!studyEnded && isInStudy) {
            floatWidgetService.onDestroy()
            registerBroadcastReciever(false)
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
