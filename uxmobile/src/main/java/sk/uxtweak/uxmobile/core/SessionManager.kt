package sk.uxtweak.uxmobile.core

import android.app.Application
import android.content.Context
import android.os.SystemClock
import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sk.uxtweak.uxmobile.BuildConfig
import sk.uxtweak.uxmobile.displaySize
import sk.uxtweak.uxmobile.lifecycle.ApplicationLifecycle
import sk.uxtweak.uxmobile.media.ScreenRecorder
import sk.uxtweak.uxmobile.media.VideoFormat
import sk.uxtweak.uxmobile.model.SessionEvent
import sk.uxtweak.uxmobile.model.events.Event
import sk.uxtweak.uxmobile.net.WebSocketClient
import sk.uxtweak.uxmobile.study.float_widget.FloatWidgetClickObserver
import java.nio.ByteBuffer

@OptIn(ExperimentalStdlibApi::class)
class SessionManager(application: Application, floatWidgetClickObserver: FloatWidgetClickObserver) {
    private val socket = WebSocketClient(BuildConfig.COLLECTOR_URL)
    private val connection = ConnectionManager(socket)
    private val collector = EventRecorder(application, floatWidgetClickObserver)
    private val recorder: ScreenRecorder

    private val collectedEvents = ArrayDeque<SessionEvent>()

    /*
     * Connection manager - can connect to server and handle automatic reconnection along with API to emit data
     * Activity connector - connects to callbacks for current activity
     * Event converter and interceptor - catches all events and converts them to event suitable to send
     * Screen recorder - register background job that takes current activity and captures
     *  screenshots and encodes them to video that is received in registered callback
     *
     * Contained classes - modular - clean and easy to use API for user (also testable)
     * Handle background jobs more explicitly, or make them more robust and reliable (that they
     *  can be controlled easily and via API even if not directly)
     * No class should start some background job that is doing something in it's constructor
     *  (constructors should be used only for instance initialization)
     *
     * SessionManager should be more modularized (split up) and should provide API for controlling
     *  session recording or other classes that provide other related API to control plugin work
     *
     * Test why sometimes plugin doesn't communicate with server even after supposed successful
     *  connection
     */

    init {
        collector.addOnEventListener(::onEvent)

        connection.startAutoConnection()
        startCollectingEvents()

        val size = application.displaySize
        recorder = ScreenRecorder(VideoFormat(size.width, size.height))
        recorder.setOnBufferReady(::onVideoBuffer)
    }

    fun startCollectingEvents() {
        collector.registerObserver(ApplicationLifecycle)
    }

    fun stopCollectingEvents() {
        collector.unregisterObserver(ApplicationLifecycle)
    }

    fun startRecordingVideo() {
        recorder.start()
    }

    fun stopRecordingVideo(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        recorder.stop()
    }

    fun startSendingEvents() {
        // - Get generated session ID from server (try until received, otherwise cannot send events)
        // - Start putting events into EventSender to send all collected events
        // - Add all received events into EventSender
    }

    fun stopSendingEvents() {
        // Stop sending events to EventSender and collect them in deque instead (if collecting is
        // enabled)
    }

    private fun onEvent(event: Event) {
        collectedEvents.add(SessionEvent(null, SystemClock.elapsedRealtime(), event))
    }

    private fun onVideoBuffer(buffer: ByteBuffer) {
        val data = Base64.encodeToString(buffer.array(), Base64.DEFAULT)
        onEvent(Event.VideoChunkEvent(data))
    }
}
