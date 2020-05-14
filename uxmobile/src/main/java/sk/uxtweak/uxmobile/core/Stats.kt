package sk.uxtweak.uxmobile.core

import android.content.Context
import android.os.SystemClock
import sk.uxtweak.uxmobile.UxMobile
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.logd
import java.text.DecimalFormat

object Stats {
    private const val TAG = "UxStats"

    private lateinit var context: Context
    private val format = DecimalFormat("0.##")

    private var isFirstFrame = true
    private var firstFrameTime = 0L
    private var lastFrameTime = 0L
    private var framesEncoded = 0
    private var keyFrames = 0
    private var connected = false

    private val videoTime: String
        get() = "${format.format((lastFrameTime - firstFrameTime) / 1000F)} s"

    fun init(context: Context) {
        this.context = context
    }

    suspend fun log() = buildString {
        append("API key: ${UxMobile.apiKey}\n")
        append("Connected to server: ${if (connected) "yes" else "no"}\n")
        append("Current session ID: ${UxMobile.sessionManager.sessionId}\n")
        append("Is recording: yes/no (Recording ID: ${UxMobile.sessionManager.persister.recordingId})\n")
        append("Events in memory: ${UxMobile.sessionManager.persister.eventsCount}\n")
        append("Video time: $videoTime\n")
        append("Frames encoded: $framesEncoded (Key frames: $keyFrames)\n")
        append("\nDatabase recordings:\n${UxMobile.sessionManager.persister.fetchDatabaseStats().joinToString("\n", postfix = "\n")}")
        append("\nVideo recordings:\n${videoSession()}\n")
    }

    fun onConnected() {
        connected = true
    }

    fun onDisconnected() {
        connected = false
    }

    fun onStartRecording() {
        logd(TAG, "Recording started")
        isFirstFrame = true
        firstFrameTime = 0L
        lastFrameTime = 0L
        framesEncoded = 0
        keyFrames = 0
    }

    fun onStopRecording() = logd(TAG, "Recording stopped")

    fun onFrameEncoded(frame: EncodedFrame) {
        if (isFirstFrame) {
            isFirstFrame = false
            firstFrameTime = SystemClock.elapsedRealtime()
        }
        lastFrameTime = SystemClock.elapsedRealtime()
        ++framesEncoded
        if (frame.isKeyFrame) {
            ++keyFrames
        }
    }

    private fun videoSession() = buildString {
        context.filesDir.listFiles()?.sortedDescending()?.forEach { file ->
            append("${file.name} (${file.list()?.size ?: 0}) (${file.listFiles()?.sorted()?.joinToString { it.name }})\n")
        }
    }
}
