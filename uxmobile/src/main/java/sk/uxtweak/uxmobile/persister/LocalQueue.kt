package sk.uxtweak.uxmobile.persister

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import sk.uxtweak.uxmobile.util.TAG
import sk.uxtweak.uxmobile.util.logw

sealed class LocalQueueCommand {
    data class Element<E>(val element: E) : LocalQueueCommand()
    object LockQueue : LocalQueueCommand()
}

@OptIn(ExperimentalStdlibApi::class)
class LocalQueue<E>(private val limit: Int) {
    private var channel = Channel<LocalQueueCommand>(Channel.UNLIMITED)
    private val queue = ArrayDeque<E>(limit)
    private var onLimitReached: suspend (ArrayDeque<E>) -> Unit = {}
    private var lockAction: suspend (ArrayDeque<E>) -> Unit = {}
    private var onQueueUnlocked: () -> Unit = {}
    private lateinit var job: Job

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("UNCHECKED_CAST")
    fun start() {
        // TODO: Doesn't send files after stop and start called
        if (channel.isClosedForSend || channel.isClosedForReceive) {
            channel = Channel(Channel.UNLIMITED)
        }
        job = GlobalScope.launch {
            try {
                for (command in channel) {
                    when (command) {
                        is LocalQueueCommand.Element<*> -> {
                            queue.add(command.element as E)
                            if (queue.size >= limit) {
                                onLimitReached(queue)
                            }
                        }
                        is LocalQueueCommand.LockQueue -> {
                            lockAction(queue)
                            onQueueUnlocked()
                        }
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

    fun insert(element: E) = channel.offer(LocalQueueCommand.Element(element))

    fun doOnLimitReached(listener: suspend (ArrayDeque<E>) -> Unit) {
        onLimitReached = listener
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun withLock(action: suspend (ArrayDeque<E>) -> Unit) {
        if (channel.isClosedForSend) {
            logw(TAG, "Channel closed")
            return
        }
        suspendCancellableCoroutine<Unit> {
            onQueueUnlocked = {
                it.resumeWith(Result.success(Unit))
            }
            lockAction = action
            channel.offer(LocalQueueCommand.LockQueue)
        }
    }
}
