package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.app.Application
import com.fasterxml.uuid.Generators
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
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
            startAll()
        }

        override fun onLastActivityStopped(activity: Activity) {
            super.onLastActivityStopped(activity)
            stopAll()
            // Maybe make session ID null when all modules are stopped
            // Check which modules use session ID (Only persister?)
        }
    }

    init {
        ApplicationLifecycle.addObserver(observer)

        val size = application.displaySize
        screenRecorder = ScreenRecorder(VideoFormat(size.width, size.height))

        database = AppDatabase.create(application)
        persister = Persister(this, eventRecorder, screenRecorder, database)
        sender = EventSender(this, connectionManager, persister, database)
    }

    private fun startAll() {
//        persister.start()
//        if (!sender.isRunning) {
//            sender.start()
//        }
//        if (!connectionManager.isRunning) {
//            connectionManager.start()
//        }
    }

    private fun stopAll() {
        persister.stop()
    }

    fun startRecording() {

    }

    fun stopRecording() {

    }

    fun addEventListener(function: (Event) -> Unit) = eventRecorder.addOnEventListener(function)

    fun generateSessionId() {
        sessionId = Generators.timeBasedGenerator().generate().toString()
    }
}
