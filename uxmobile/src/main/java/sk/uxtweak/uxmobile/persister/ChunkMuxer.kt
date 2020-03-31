package sk.uxtweak.uxmobile.persister

import android.media.MediaFormat
import android.media.MediaMuxer
import sk.uxtweak.uxmobile.core.logd
import sk.uxtweak.uxmobile.core.loge
import sk.uxtweak.uxmobile.core.logi
import sk.uxtweak.uxmobile.core.logw
import sk.uxtweak.uxmobile.recorder.screen.isKeyFrame
import sk.uxtweak.uxmobile.util.NamedThreadFactory
import java.io.File
import java.util.concurrent.*

class ChunkMuxer(private val filesPath: String, private val keyFramesInOneChunk: Int = 1) {
    private val executor = Executors.newSingleThreadExecutor(
        NamedThreadFactory("Muxer thread", Thread.MAX_PRIORITY)
    )
    private val queue: BlockingQueue<MuxerCommand> = LinkedBlockingQueue()
    private var index = -1
    private var muxer: MediaMuxer? = null
    private var trackIndex = -1
    private var currentKeyFrame = keyFramesInOneChunk
    private lateinit var future: Future<*>
    private lateinit var format: MediaFormat

    private val job = Runnable {
        try {
            job@ while (true) {
                when (val command = queue.take()) {
                    is MuxerCommand.MuxFrame -> {
                        if (command.frame.isKeyFrame) {
                            if (++currentKeyFrame >= keyFramesInOneChunk) {
                                currentKeyFrame = 0
                                restartMuxer()
                            }
                        }
                        muxer!!.writeSampleData(
                            trackIndex,
                            command.frame.buffer,
                            command.frame.bufferInfo
                        )
                    }
                    is MuxerCommand.ChangeOutputFormat -> {
                        logd(
                            TAG,
                            "Muxer output format changed"
                        )
                        format = command.format
                    }
                    is MuxerCommand.StopMuxer -> {
                        logd(
                            TAG,
                            "Stopping chunk muxer"
                        )
                        muxer?.stop()
                        muxer?.release()
                        break@job
                    }
                }
            }
        } catch (exception: Exception) {
            loge(
                TAG,
                "ChunkMuxer exception",
                exception
            )
            throw exception
        }
    }

    fun postCommand(command: MuxerCommand) {
        if (!::future.isInitialized) {
            logw(
                TAG,
                "Posting command $command to muxer that is not started!"
            )
        }
        if (!queue.offer(command)) {
            throw IllegalStateException("Cannot insert command into queue!")
        }
    }

    fun start() {
        logd(
            TAG,
            "Starting muxer"
        )
        if (::future.isInitialized) {
            throw IllegalStateException("Muxer already started!")
        }
        future = executor.submit(job)
    }

    fun stop() {
        logd(
            TAG,
            "Stopping muxer"
        )
        if (!::future.isInitialized) {
            throw IllegalStateException("Muxer is not started!")
        }
        postCommand(MuxerCommand.StopMuxer)
    }

    fun join() {
        if (!::future.isInitialized) {
            throw IllegalStateException("Muxer is not started!")
        }
        try {
            future.get(
                SHUTDOWN_TIMEOUT,
                SHUTDOWN_TIME_UNIT
            )
        } catch (exception: TimeoutException) {
            logw(
                TAG,
                "Timeout when waiting to finish muxer job, shutting down executor!"
            )
            shutdownExecutor()
        }
    }

    private fun shutdownExecutor() {
        executor.shutdown()
        if (!executor.awaitTermination(
                SHUTDOWN_TIMEOUT,
                SHUTDOWN_TIME_UNIT
            )) {
            logw(
                TAG,
                "Executor didn't shutdown gracefully in time limit, shutting down forcefully!"
            )
            val message = buildString {
                append("Tasks that didn't shutdown gracefully: ")
                executor.shutdownNow().also {
                    append("Task: $it")
                }
            }
            logi(
                TAG,
                message
            )
        }
    }

    private fun restartMuxer() {
        logd(
            TAG,
            "Saving old chunk and creating new chunk"
        )
        muxer?.stop()
        muxer?.release()
        muxer = MediaMuxer(
            getPath(++index),
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
        trackIndex = muxer!!.addTrack(format)
        muxer!!.start()
    }

    private fun getPath(index: Int) = File(filesPath, "$index.mp4").path

    companion object {
        private const val TAG = "UxMobile"
        private const val SHUTDOWN_TIMEOUT = 3L
        private val SHUTDOWN_TIME_UNIT = TimeUnit.SECONDS
    }
}

fun ChunkMuxer.stopAndJoin() {
    stop()
    join()
}