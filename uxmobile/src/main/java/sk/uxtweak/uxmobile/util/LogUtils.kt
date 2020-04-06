package sk.uxtweak.uxmobile.util

object LogUtils {
    val logs = StringBuilder()
    private val listeners = mutableListOf<(String) -> Unit>()

    fun append(message: String) {
        logs.append("$message\n")
        listeners.forEach { it(message) }
    }

    fun addLogListener(listener: (String) -> Unit) {
        listeners += listener
    }

    fun removeLogListener(listener: (String) -> Unit) {
        listeners -= listener
    }
}
