package sk.uxtweak.uxmobile.core

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import sk.uxtweak.uxmobile.recorder.screen.EncodedFrame
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.logd
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object Stats {
    private const val TAG = "UxStats"
    private const val PRINT_DELAY = 10L
    private val PRINT_TIME_UNIT = TimeUnit.SECONDS

    private val format = DecimalFormat("0.##")

    private var isFirstFrame = true
    private var firstFrameTime = 0L
    private var lastFrameTime = 0L
    private var framesEncoded = 0
    private var keyFrames = 0

    private val videoTime: String
        get() = "${format.format((lastFrameTime - firstFrameTime) / 1000F)} s"

    fun init(context: Context) {
        GlobalScope.withFixedDelay(Dispatchers.IO, PRINT_TIME_UNIT.toMillis(PRINT_DELAY)) {
            logd(TAG, "Video time: $videoTime")
            logd(TAG, "Frames encoded: $framesEncoded (Key frames: $keyFrames)")
            logd(TAG, "Files : ${context.fileList().joinToString()}")
        }
    }

    fun onStartRecording() = logd(TAG, "Recording started")

    fun onStopRecording() = logd(TAG, "Recording stopped")

    fun onVideoChunk(path: String) = logd(TAG, "Recording $path saved")

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
}
