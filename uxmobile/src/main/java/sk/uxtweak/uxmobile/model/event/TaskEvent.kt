package sk.uxtweak.uxmobile.model.event

import org.json.JSONArray
import sk.uxtweak.uxmobile.model.study.Task

class TaskEvent(
    startTime: Long,
    private val taskId: Long,
    private val status: Status
) : Event(startTime) {
    constructor(startTime: Long, task: Task, status: Status) : this(startTime, task.id, status)

    override fun getType() = TYPE_TASK

    override fun toJson(): JSONArray = super.toJson()
        .put(INDEX_TASK_STATUS, status.toString())
        .put(INDEX_TASK_ID, taskId)

    enum class Status {
        STARTED, COMPLETED, CANCELLED, SKIPPED;

        override fun toString() = when (this) {
            STARTED -> STATUS_STARTED
            COMPLETED -> STATUS_COMPLETED
            CANCELLED -> STATUS_CANCELLED
            SKIPPED -> STATUS_SKIPPED
        }
    }

    companion object {
        const val INDEX_TASK_STATUS = 2
        const val INDEX_TASK_ID = 3

        const val STATUS_STARTED = "s"
        const val STATUS_COMPLETED = "c"
        const val STATUS_CANCELLED = "n"
        const val STATUS_SKIPPED = "k"
    }
}
