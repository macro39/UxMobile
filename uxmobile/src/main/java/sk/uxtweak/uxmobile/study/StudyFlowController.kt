package sk.uxtweak.uxmobile.study

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Handler
import android.util.Log
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.core.SessionManager
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.LifecycleObserver
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetService
import sk.uxtweak.uxmobile.study.model.StudyTask
import sk.uxtweak.uxmobile.study.persister.QuestionAnswerDatabase
import sk.uxtweak.uxmobile.study.sender.QuestionAnswerSender
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

/**
 * Created by Kamil Macek on 12. 11. 2019.
 */
class StudyFlowController(
    val context: Context,
    val sessionManager: SessionManager
) : LifecycleObserver, FloatWidgetClickObserver {

    companion object {
        lateinit var database: QuestionAnswerDatabase
    }

    private val TAG = this::class.java.simpleName

    private var isInStudy = false
    private var studyEnded = false
    private var minimizedWhenInStudyFlow = false

    private var isStarted = false
    private var isStudySet = true

    private val sender: QuestionAnswerSender
    private val floatWidgetService: FloatWidgetService

    private lateinit var broadcastReceiver: BroadcastReceiver

    init {
        ApplicationLifecycle.addObserver(this)

        sessionManager.addEventListener {
            when (it) {
                is Event.TapEvent, is Event.LongPressEvent -> onClick()
            }
        }

        floatWidgetService = FloatWidgetService(context, this)
        floatWidgetService.onInit()

        setupBroadcastReceiver()

        database = QuestionAnswerDatabase.create(context)
        sender = QuestionAnswerSender(UxMobile.adonisWebSocketClient, database)

        if (!sender.isRunning) {
            sender.start()
        }

        Log.d(TAG, "Configured")
    }

    fun start() {
        isStarted = true
        studyStateResolver()
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.d(TAG, "BROADCAST RECEIVED")

                val action = intent?.action

                if (Constants.RECEIVER_IN_STUDY == action) {
                    if (intent.getBooleanExtra(Constants.RECEIVER_ASK_LATER, true)) {
                        waitForNextAskForTakingPartInStudy()
                        return
                    }
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
            sessionManager.startRecording(StudyDataHolder.study?.studyId!!)
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

//        UxMobile.adonisWebSocketClient.closeConnection()
//        sender.stop()

        Log.d(TAG, "STUDY ENDED")
    }

    override fun taskExecutionEnded(successfully: Boolean) {
        Log.d(TAG, "TASK EXECUTION ENDED")
        floatWidgetService.setVisibility(false)

        sessionManager.stopRecording()

        // set executed task as accomplished
        val selectedStudyTask: StudyTask =
            StudyDataHolder.tasks?.single { s -> s.name == StudyDataHolder.doingTaskWithName }!!
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

    private fun waitForNextAskForTakingPartInStudy() {
        Handler().postDelayed({
            studyStateResolver()
        }, 3000)
    }

    private fun studyStateResolver() {
        if (!studyEnded) {
            registerBroadcastReciever(true)

            if (minimizedWhenInStudyFlow) {
                minimizedWhenInStudyFlow = false
            } else {
                if (!isInStudy) {
                    showStudyFlow(onlyInstructions = false, endOfTask = false)
                } else {
                    sessionManager.startRecording(StudyDataHolder.study?.studyId!!)
                    floatWidgetService.onCreate()
                }
            }
        }
    }

    override fun onFirstActivityStarted(activity: Activity) {
        if (isStarted) {
            studyStateResolver()
        }
    }

    override fun onAnyActivityStarted(activity: Activity) {
    }

    override fun onAnyActivityStopped(activity: Activity) {
    }

    override fun onLastActivityStopped(activity: Activity) {
        // if user minimize app when study flow is on
        if (StudyFlowFragmentManager::class.qualifiedName.equals(activity.localClassName)) {
            minimizedWhenInStudyFlow = true
            return
        }

        if (!studyEnded && isInStudy) {
            sessionManager.stopRecording()
            floatWidgetService.onDestroy()
            registerBroadcastReciever(false)
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
