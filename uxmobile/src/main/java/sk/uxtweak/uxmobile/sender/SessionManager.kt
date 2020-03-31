package sk.uxtweak.uxmobile.sender

import android.app.Activity
import android.app.Application
import android.os.SystemClock
import android.util.Base64
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.core.Stats
import sk.uxtweak.uxmobile.core.displaySize
import sk.uxtweak.uxmobile.core.logd
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.lifecycle.LifecycleObserverAdapter
import sk.uxtweak.uxmobile.model.Event
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.net.ConnectionManager
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.recorder.events.EventRecorder
import sk.uxtweak.uxmobile.recorder.screen.ScreenRecorder
import sk.uxtweak.uxmobile.recorder.screen.VideoFormat
import java.nio.ByteBuffer

@OptIn(ExperimentalStdlibApi::class)
class SessionManager(application: Application) {
    private val socket = WebSocketClient(BuildConfig.COLLECTOR_URL)
    private val connection = ConnectionManager(socket)
    private val collector =
        EventRecorder(application)
    private val recorder: ScreenRecorder

    private val collectedEvents = ArrayDeque<SessionEvent>()

    init {
        connection.startAutoConnection()

        collector.addOnEventListener(::onEvent)
        collector.registerObserver(ApplicationLifecycle)

        val path = application.filesDir.absolutePath
        val size = application.displaySize
        recorder = ScreenRecorder(path, VideoFormat(size.width, size.height))
        recorder.setOnChunkReady(::onVideoChunk)

        ApplicationLifecycle.addObserver(object : LifecycleObserverAdapter() {
            override fun onLastActivityStopped(activity: Activity) {
                logd("UxMobile", "Stopping recorder")
                recorder.stop()
            }
        })
    }

    fun startRecording() {
        Stats.onStartRecording()
        recorder.start()
    }

    fun stopRecording() {
        Stats.onStopRecording()
        recorder.stop()
    }

    private fun onEvent(event: Event) {
        collectedEvents.add(SessionEvent(null, SystemClock.elapsedRealtime(), event))
    }

    private fun onVideoChunk(path: String) {
        Stats.onVideoChunk(path)
    }

    private fun convertToVideoEvent(videoData: ByteBuffer) {
        val data = Base64.encodeToString(videoData.array(), Base64.DEFAULT)
        onEvent(Event.VideoChunkEvent(data))
    }

    fun addEventListener(function: (Event) -> Unit) {
        collector.addOnEventListener(function)
    }
}