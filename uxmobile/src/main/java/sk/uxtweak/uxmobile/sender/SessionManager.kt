package sk.uxtweak.uxmobile.sender

import android.app.Activity
import android.app.Application
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.displaySize
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
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logd

class SessionManager(application: Application) {
    private val socket = WebSocketClient(BuildConfig.COLLECTOR_URL)
    private val connection = ConnectionManager(socket)
    private val eventRecorder = EventRecorder(application, ApplicationLifecycle)
    private val screenRecorder: ScreenRecorder
    private val database: AppDatabase
    private lateinit var persister: Persister

    private val observer = object : LifecycleObserverAdapter() {
        override fun onFirstActivityStarted(activity: Activity) {
            logd(TAG, "Generating new session ID for persister")
            persister.generateNewSessionId()
        }
    }

    init {
        ApplicationLifecycle.addObserver(observer)

        connection.startAutoConnection()

        eventRecorder.start()

        val size = application.displaySize
        screenRecorder = ScreenRecorder(VideoFormat(size.width, size.height))

        database = AppDatabase.create(application, true)
        persister = Persister(eventRecorder, screenRecorder, database)
        persister.start()

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
