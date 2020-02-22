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
import sk.uxtweak.uxmobile.study.model.Study
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

        val gson = GsonBuilder().create()
        val dummyStudyResponse: Study =
            gson.fromJson(dummyResponseData(), Study::class.java)

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

        StudyDataHolder.study = dummyStudyResponse

        StudyDataHolder.numberOfTasks = StudyDataHolder.tasks.size
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
            "            ],\n" +
            "            \"rule_values\": [\n" +
            "                \"man\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"2\",\n" +
            "            \"question_required\": true,\n" +
            "            \"description\": \"How old are you?\",\n" +
            "            \"answer_type\": \"radiobtn\",\n" +
            "            \"answer_required\": true,\n" +
            "            \"reason_needed\": false,\n" +
            "            \"question_options\": [\n" +
            "                \"<18\",\n" +
            "                \">18\"\n" +
            "            ],\n" +
            "            \"rule_values\": [\n" +
            "                \">18\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}"

        val gson = GsonBuilder().create()
        val dummyQuestionnaireRules: QuestionnaireRules =
            gson.fromJson(questionnaireRules, QuestionnaireRules::class.java)

        StudyDataHolder.questionnaireRules = dummyQuestionnaireRules
    }

    private fun dummyResponseData(): String {
        return "{\n" +
            "    \"studyId\": 1,\n" +
            "    \"name\": \"STUDIA C. 1 POSTGRES\",\n" +
            "    \"studyBrandings\": {\n" +
            "        \"primaryColor\": \"#008577\",\n" +
            "        \"secondaryColor\": \"#FFF57C00\",\n" +
            "        \"hibernateLazyInitializer\": {}\n" +
            "    },\n" +
            "    \"studyTasks\": [\n" +
            "        {\n" +
            "            \"taskId\": 1,\n" +
            "            \"title\": \"TASK NO. 1\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"taskId\": 2,\n" +
            "            \"title\": \"TASK NO. 2\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"studyMessages\": [\n" +
            "        {\n" +
            "            \"type\": \"completed\",\n" +
            "            \"title\": \"Thank you, that's all!\",\n" +
            "            \"content\": \"All done, awesome! Thanks again for your participation. Your feedback is incredibly useful in helping us understand how people interact with our app, so that we can make our application easier to use.\\n\\nYou may now going back to your work!\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"welcome\",\n" +
            "            \"title\": \"Welcome!\",\n" +
            "            \"content\": \"Welcome to this study, and thank you for agreeing to participate! The activity shouldn't take longer than 30 to 60 minutes to complete. Your response will help us to better understand how people behave in our app.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"closed\",\n" +
            "            \"title\": \"Sorry, this study has concluded\",\n" +
            "            \"content\": \"Sorry, this study has concluded\\nThis study has been closed and so it's no longer possible to participate. If you think that this is a mistake and you should still be able to participate, please contact the conductor of the study. We hope to see you again.\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"rejected\",\n" +
            "            \"title\": \"Thank you and hope we see you next time!\",\n" +
            "            \"content\": \"We are a little sad, but also next day is there a opportunity to change your favorite application by participating in study!\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"instructions\",\n" +
            "            \"title\": \"Instructions\",\n" +
            "            \"content\": \"<b>Here's how it works:</b><ol><li>You will be presented with a task.</li><li>After reading the task, you will be redirected to a website.</li><li>Click through the website as you naturally would in order to fulfill the task.</li><li>Once you arrive at the intended destination, click <b>Task done</b> and the task will end.</li><li>Repeat the previous steps for all the studyTasks to complete the RePlay study.</li></ol><em>This is not a test of your ability, there are no right or wrong answers.</em><br><b>That's it, let's get started!</b></div>\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"studyQuestions\": [\n" +
            "        {\n" +
            "            \"type\": \"screening_qst\",\n" +
            "            \"atTask\": 1,\n" +
            "            \"title\": \"DOTAZNIK PARTICIPANTA\",\n" +
            "            \"description\": \"Vyplne prosim dotaznik, aby sme mohli zistit, ci ste vhodnym respondentom\",\n" +
            "            \"order\": 1,\n" +
            "            \"answerType\": \"dropdown\",\n" +
            "            \"answerOptions\": \"{\\\"id\\\":123,\\\"title\\\":\\\"AKE MAS POHLAVIE?\\\",\\\"options\\\":[\\\"MUZ\\\", \\\"ZENA\\\"] }\",\n" +
            "            \"required\": true,\n" +
            "            \"randomizeOptions\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"tasks_qst\",\n" +
            "            \"atTask\": 1,\n" +
            "            \"title\": \"DOTAZNIK PO ULOHE\",\n" +
            "            \"description\": \"Vyplne prosim dotaznik, aby sme mohli zistit, ci ste vhodnym respondentom\",\n" +
            "            \"order\": 1,\n" +
            "            \"answerType\": \"radiobtn\",\n" +
            "            \"answerOptions\": \"{\\\"id\\\":123,\\\"title\\\":\\\"AKE MAS POHLAVIE?\\\",\\\"options\\\":[\\\"MUZ\\\", \\\"ZENA\\\"] }\",\n" +
            "            \"required\": true,\n" +
            "            \"randomizeOptions\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"pre_qst\",\n" +
            "            \"atTask\": 1,\n" +
            "            \"title\": \"DOTAZNIK PRED STUDIOU\",\n" +
            "            \"description\": \"Vyplne prosim dotaznik, aby sme mohli zistit, ci ste vhodnym respondentom\",\n" +
            "            \"order\": 1,\n" +
            "            \"answerType\": \"textarea\",\n" +
            "            \"answerOptions\": \"{\\\"id\\\":123,\\\"title\\\":\\\"AKE MAS POHLAVIE?\\\",\\\"options\\\":[\\\"MUZ\\\", \\\"ZENA\\\"] }\",\n" +
            "            \"required\": true,\n" +
            "            \"randomizeOptions\": true\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"post_qst\",\n" +
            "            \"atTask\": 1,\n" +
            "            \"title\": \"DOTAZNIK PO STUDII\",\n" +
            "            \"description\": \"Vyplne prosim dotaznik, aby sme mohli zistit, ci ste vhodnym respondentom\",\n" +
            "            \"order\": 1,\n" +
            "            \"answerType\": \"radiobtn\",\n" +
            "            \"answerOptions\": \"{\\\"id\\\":123,\\\"title\\\":\\\"AKE MAS POHLAVIE?\\\",\\\"options\\\":[\\\"MUZ\\\", \\\"ZENA\\\"] }\",\n" +
            "            \"required\": true,\n" +
            "            \"randomizeOptions\": true\n" +
            "        }\n" +
            "    ],\n" +
            "    \"hibernateLazyInitializer\": {}\n" +
            "}"
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

    override fun taskExecutionEnded() {
        Log.d(TAG, "TASK EXECUTION ENDED")
        floatWidgetService.setVisibility(false)

        // set executed task as accomplished
        val selectedStudyTask: StudyTask =
            StudyDataHolder.tasks.single { s -> s.taskId == StudyDataHolder.doingTaskWithId.toLong() }
        selectedStudyTask.accomplished = true

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
