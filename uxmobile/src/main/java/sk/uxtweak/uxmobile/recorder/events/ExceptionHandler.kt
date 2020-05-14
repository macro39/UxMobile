package sk.uxtweak.uxmobile.recorder.events

import android.util.Log

class ExceptionHandler(
    private val exceptionHandler: Thread.UncaughtExceptionHandler?
) : Thread.UncaughtExceptionHandler {
    private var listener: (Thread, Throwable) -> Unit = { _, _ -> }

    fun setListener(listener: (Thread, Throwable) -> Unit) {
        this.listener = listener
    }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        Log.e(TAG, "uncaughtException: ", exception)
        exception.printStackTrace()

        listener(thread, exception)

        exceptionHandler?.uncaughtException(thread, exception)
    }

    companion object {
        private const val TAG = "UxMobile"

        @JvmStatic
        fun register() {
            val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (currentHandler !is ExceptionHandler) {
                Thread.setDefaultUncaughtExceptionHandler(
                    ExceptionHandler(
                        currentHandler
                    )
                )
            }
        }

        @JvmStatic
        fun unregister() = Thread.setDefaultUncaughtExceptionHandler(handler!!.exceptionHandler)

        val handler: ExceptionHandler?
            get() {
                val currentHandler = Thread.getDefaultUncaughtExceptionHandler()
                if (currentHandler != null && currentHandler is ExceptionHandler) {
                    return currentHandler
                }
                return null
            }

        fun setHandlerListener(listener: (Thread, Throwable) -> Unit) =
            handler?.setListener(listener)
    }
}
