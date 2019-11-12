package sk.uxtweak.uxmobile

import android.util.Log

// TODO: Test this class
class SessionExceptionHandler(
    private val exceptionHandler: Thread.UncaughtExceptionHandler
) : Thread.UncaughtExceptionHandler {
    private var listener: (Thread, Throwable) -> Unit = { _, _ -> }

    fun setListener(listener: (Thread, Throwable) -> Unit) {
        this.listener = listener
    }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        Log.e(TAG, "uncaughtException: ", exception)
        exception.printStackTrace()

        listener(thread, exception)

        exceptionHandler.uncaughtException(thread, exception)
    }

    companion object {
        private const val TAG = "UxMobile"

        @JvmStatic
        fun register() {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (currentHandler != null && currentHandler !is SessionExceptionHandler) {
                Thread.setDefaultUncaughtExceptionHandler(
                    SessionExceptionHandler(currentHandler)
                )
            }
        }

        val handler: SessionExceptionHandler?
            get() {
                val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
                if (currentHandler != null && currentHandler is SessionExceptionHandler) {
                    return currentHandler
                }
                return null
            }
    }
}
