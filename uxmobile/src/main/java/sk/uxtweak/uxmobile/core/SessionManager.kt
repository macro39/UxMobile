package sk.uxtweak.uxmobile.core

import android.app.Activity
import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.persister.Persister
import sk.uxtweak.uxmobile.persister.room.AppDatabase
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
    lateinit var persister: Persister
    val sender: EventSender
    private val database: AppDatabase

    private val observer = object : LifecycleObserverAdapter() {
        override fun onFirstActivityStarted(activity: Activity) {
            startAll()
            logd(TAG, "Generating new session ID for persister")
            persister.generateNewSessionId()
        }

        override fun onLastActivityStopped(activity: Activity) {
            super.onLastActivityStopped(activity)
            stopAll()
        }
    }

    init {
        ApplicationLifecycle.addObserver(observer)

        val size = application.displaySize
        screenRecorder = ScreenRecorder(VideoFormat(size.width, size.height))

        database = AppDatabase.create(application, false)
        persister = Persister(eventRecorder, screenRecorder, database)

        sender = EventSender(connectionManager, persister, database)
    }

    private fun startAll() {
        persister.start()
        eventRecorder.start()
        screenRecorder.start()
        if (!sender.isRunning) {
            sender.start()
        }
        if (!connectionManager.isRunning) {
            connectionManager.start()
        }
    }

    private fun stopAll() {
        eventRecorder.stop()
        screenRecorder.stop()
        persister.stop()
    }

    fun startRecording() {

    }

    fun stopRecording() {

    }

    fun addEventListener(function: (Event) -> Unit) = eventRecorder.addOnEventListener(function)
}
