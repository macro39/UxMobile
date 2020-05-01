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
    private val sessionManager: SessionManager
) : LifecycleObserver, FloatWidgetClickObserver {

    companion object {
        lateinit var database: QuestionAnswerDatabase
        lateinit var sender: QuestionAnswerSender
        var isStudySet = true                   // when no tasks in study
    }

    private val TAG = this::class.java.simpleName

    private var isOnlyRecording = false         // when recording, no tasks in study

    private var isTakingPartInStudy = false
    private var studyAlreadyEnded = false

    private var isMinimizedWhenInStudyFlow = false

    private var isControllerStarted = false

    private var isWaitingForNextAskForTakingPartInStudy = false

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

        Log.d(TAG, "Configured")
    }

    fun start() {
        isControllerStarted = true

        if (StudyDataHolder.study != null && StudyDataHolder.tasks.isNullOrEmpty()) {
            isStudySet = false
        } else {
            isStudySet = true
            if (!sender.isRunning) {
                sender.start()
            }
        }

        studyStateResolver()
    }

    private fun setupBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
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
                        if (!isStudySet) {
                            startOnlyRecording()
                            return
                        } else {
                            studyAccepted(true)
                        }
                    } else {
                        studyAccepted(false)
                    }
                }
            }
        }
    }

    private fun registerBroadcastReceiver(register: Boolean) {
        if (register) {
            context.registerReceiver(broadcastReceiver, IntentFilter(Constants.RECEIVER_IN_STUDY))
        } else {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    private fun startOnlyRecording() {
        Log.d(TAG, "Only recording, no tasks found")
        isOnlyRecording = true

        // end sender and close socket
        if (sender.isRunning) {
            sender.stop()
        }
        UxMobile.adonisWebSocketClient.closeConnection()

        startOnlyRecording(true)
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
            if (!isTakingPartInStudy) {
                isTakingPartInStudy = true
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
        isTakingPartInStudy = false
        studyAlreadyEnded = true
        floatWidgetService.onDestroy()
        registerBroadcastReceiver(false)

        if (sender.isRunning) {
            sender.lastDataToSend = true
        } else {
            UxMobile.adonisWebSocketClient.closeConnection()
        }

        Log.d(TAG, "STUDY ENDED")
    }

    override fun taskExecutionEnded(successfully: Boolean) {
        Log.d(TAG, "TASK EXECUTION ENDED")
        floatWidgetService.setVisibility(false)

        sessionManager.stopRecording()

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
        if (isTakingPartInStudy) {
            floatWidgetService.changeFloatButtonState(false)
        }
    }

    private fun showStudyFlow(onlyInstructions: Boolean, endOfTask: Boolean) {
        registerBroadcastReceiver(true)
        val intent = Intent(context, StudyFlowFragmentManager::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(EXTRA_IS_STUDY_SET, isStudySet)
        intent.putExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, onlyInstructions)
        intent.putExtra(EXTRA_END_OF_TASK, endOfTask)
        context.startActivity(intent)
    }

    private fun waitForNextAskForTakingPartInStudy() {
        isWaitingForNextAskForTakingPartInStudy = true
        Handler().postDelayed({
            isWaitingForNextAskForTakingPartInStudy = false
            studyStateResolver()
        }, 3000)
    }

    private fun studyStateResolver() {
        if (!studyAlreadyEnded) {
            registerBroadcastReceiver(true)

            if (isMinimizedWhenInStudyFlow) {
                isMinimizedWhenInStudyFlow = false
            } else {
                if (!isTakingPartInStudy) {
                    showStudyFlow(onlyInstructions = false, endOfTask = false)
                } else {
                    sessionManager.startRecording(StudyDataHolder.study?.studyId!!)
                    floatWidgetService.onCreate()
                }
            }
        }
    }

    private fun startOnlyRecording(start: Boolean) {
        if (start) {
            sessionManager.startRecording(null)
        } else {
            sessionManager.stopRecording()
        }
    }

    override fun onFirstActivityStarted(activity: Activity) {
        if (isWaitingForNextAskForTakingPartInStudy) {
            return
        }

        if (!isStudySet && isOnlyRecording) {
            startOnlyRecording(true)
            return
        }

        if (isControllerStarted) {
            studyStateResolver()
        }
    }

    override fun onAnyActivityStarted(activity: Activity) {
    }

    override fun onAnyActivityStopped(activity: Activity) {
    }

    override fun onLastActivityStopped(activity: Activity) {
        if (isWaitingForNextAskForTakingPartInStudy) {
            return
        }

        if (!isStudySet && isOnlyRecording) {
            startOnlyRecording(false)
            return
        }

        // if user minimize app when study flow is on
        if (StudyFlowFragmentManager::class.qualifiedName.equals(activity.localClassName)) {
            isMinimizedWhenInStudyFlow = true
            return
        }

        if (isTakingPartInStudy && !studyAlreadyEnded) {
            sessionManager.stopRecording()
            floatWidgetService.onDestroy()
            registerBroadcastReceiver(false)
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
    }
}
