package sk.uxtweak.uxmobile.concurrency

import kotlinx.coroutines.channels.Channel

class ChannelCondition : Condition {
    private val channel = Channel<Unit>()

    override fun open() {
        channel.offer(Unit)
    }

    override suspend fun block() = channel.receive()
}
