package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.app.Application
import com.fasterxml.uuid.Generators
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.ForegroundScope
import sk.uxtweak.uxmobile.lifecycle.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.database.AppDatabase
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.VideoFormat
import sk.uxtweak.uxmobile.sender.EventSender
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd
import kotlin.random.Random

class SessionManager(application: Application) {
    private val socket = WebSocketClient(BuildConfig.COLLECTOR_URL)
    val connectionManager = ConnectionManager(socket)
    val eventRecorder = EventRecorder(application, ApplicationLifecycle)
    val screenRecorder: ScreenRecorder
    val persister: Persister
    val sender: EventSender
    private val database: AppDatabase

    lateinit var sessionId: String

    private val observer = object : LifecycleObserverAdapter() {
        override fun onFirstActivityStarted(activity: Activity) {
            generateSessionId()
            logd(TAG, "Session started (generated session ID: $sessionId)")

            @Suppress("ConstantConditionIf")
            if (BuildConfig.TEST_MODE) {
                logd(TAG, "Starting recording in test mode")
                startRecording(BuildConfig.TEST_MODE_STUDY_ID)
            }
        }

        override fun onLastActivityStopped(activity: Activity) {
            @Suppress("ConstantConditionIf")
            if (BuildConfig.TEST_MODE) {
                logd(TAG, "Stopping recording in test mode")
                stopRecording()
            }
        }
    }

    init {
        @Suppress("ConstantConditionIf")
        if (BuildConfig.TEST_MODE) {
            logd(TAG, "Starting plugin in test mode")
        }
        ApplicationLifecycle.addObserver(observer)

        val size = application.displaySize
        screenRecorder = ScreenRecorder(VideoFormat(size.width, size.height))

        database = AppDatabase.create(application)
        persister = Persister(this, eventRecorder, screenRecorder, database)
        sender = EventSender(this, connectionManager, persister, database)

        sender.start()
        connectionManager.start()
    }

    fun startRecording(studyId: Int? = null) = GlobalScope.launch(Dispatchers.Main) {
        logd(TAG, "Starting recording")
        persister.start(studyId)
    }

    fun stopRecording() = GlobalScope.launch(Dispatchers.Main) {
        logd(TAG,"Stopping recording")
        persister.stop()
    }

    fun addEventListener(function: (Event) -> Unit) = eventRecorder.addOnEventListener(function)

    fun generateSessionId() {
        sessionId = Generators.timeBasedGenerator().generate().toString()
    }
}
