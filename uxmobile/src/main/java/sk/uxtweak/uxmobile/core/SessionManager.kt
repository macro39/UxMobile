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
    private val connection = ConnectionManager(socket)
    private val eventRecorder = EventRecorder(application, ApplicationLifecycle)
    private val screenRecorder: ScreenRecorder
    private val database: AppDatabase
    private lateinit var persister: Persister
    private val sender: EventSender

    private val observer = object : LifecycleObserverAdapter() {
        override fun onFirstActivityStarted(activity: Activity) {
            logd(TAG, "Generating new session ID for persister")
            persister.generateNewSessionId()
            persister.start()
        }

        override fun onLastActivityStopped(activity: Activity) {
            GlobalScope.launch(Dispatchers.IO) {
                persister.stop()
                logd(TAG, "Flushing events")
                persister.flushEvents()
                logd(TAG, "Clearing session ID")
                persister.sessionId = null
            }
        }
    }

    init {
        ApplicationLifecycle.addObserver(observer)

        connection.startAutoConnection()

        eventRecorder.start()

        val size = application.displaySize
        screenRecorder = ScreenRecorder(VideoFormat(size.width, size.height))

        database = AppDatabase.create(application, false)
        persister = Persister(eventRecorder, screenRecorder, database)

        sender = EventSender(connection, persister, database)
        sender.start()

        screenRecorder.start()
    }

    fun startRecording() {
        Stats.onStartRecording()
        screenRecorder.start()
    }

    fun stopRecording() {
        Stats.onStopRecording()
        screenRecorder.stop()
    }

    fun addEventListener(function: (Event) -> Unit) = eventRecorder.addOnEventListener(function)
}
