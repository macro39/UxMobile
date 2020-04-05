package sk.uxtweak.uxmobile.persister

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import sk.uxtweak.uxmobile.util.TAG

@OptIn(ExperimentalStdlibApi::class)
class LocalQueue<E>(private val limit: Int) {
    private val channel = Channel<E>(Channel.UNLIMITED)
    private val queue = ArrayDeque<E>(limit)
    private var onLimitReached: suspend (ArrayDeque<E>) -> Unit = {}
    private lateinit var job: Job

    fun start() {
        job = GlobalScope.launch {
            try {
                for (element in channel) {
                    queue.add(element)
                    if (queue.size >= limit) {
                        onLimitReached(queue)
                    }
                }
            } catch (exception: CancellationException) {
                Log.e(TAG, "Channel closed: ", exception)
            }
        }
    }

    fun stop(scope: CoroutineScope = GlobalScope) = scope.launch(Dispatchers.IO) {
        stopAndJoin()
    }

    suspend fun stopAndJoin() {
        channel.close()
        job.cancelAndJoin()
    }

    fun insert(element: E) = channel.offer(element)

    fun doOnLimitReached(listener: suspend (ArrayDeque<E>) -> Unit) {
        onLimitReached = listener
    }
}
