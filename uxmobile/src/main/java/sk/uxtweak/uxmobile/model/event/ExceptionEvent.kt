package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray
import java.io.PrintWriter
import java.io.StringWriter

class ExceptionEvent(startTime: Long, private val throwable: Throwable) : Event(startTime) {
    override fun getType() = TYPE_EXCEPTION

    override fun toJson(): JSONArray {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        val stackTrace = stringWriter.toString()

        return super.toJson().put(INDEX_STACKTRACE, stackTrace)
    }

    companion object {
        const val INDEX_STACKTRACE = 2
    }
}
