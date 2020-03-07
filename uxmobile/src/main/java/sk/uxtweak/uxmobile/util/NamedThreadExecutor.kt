package sk.uxtweak.uxmobile.util

import java.util.concurrent.ThreadFactory

class NamedThreadFactory(
    private val name: String,
    private val priority: Int = Thread.NORM_PRIORITY
) : ThreadFactory {
    override fun newThread(runnable: Runnable) = Thread(
        System.getSecurityManager()?.threadGroup ?: Thread.currentThread().threadGroup,
        runnable,
        name,
        0
    ).apply {
        if (isDaemon) {
            isDaemon = false
        }
        if (priority != this@NamedThreadFactory.priority) {
            priority = this@NamedThreadFactory.priority
        }
    }
}
