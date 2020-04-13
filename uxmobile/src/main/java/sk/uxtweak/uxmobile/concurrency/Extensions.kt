package sk.uxtweak.uxmobile.concurrency

import kotlinx.coroutines.sync.Mutex

inline fun Mutex.withTryLock(success: () -> Unit, failure: () -> Unit = {}) {
    val locked = tryLock()
    try {
        if (locked) {
            success()
        } else {
            failure()
        }
    } finally {
        if (locked) {
            unlock()
        }
    }
}
