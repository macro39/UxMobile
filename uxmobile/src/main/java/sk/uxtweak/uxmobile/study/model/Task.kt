package sk.uxtweak.uxmobile.study.model

/**
 * Created by Kamil Macek on 28. 1. 2020.
 */
data class Task(
    val taskId: Long,
    val description: String,
    val instruction: String,
    var accomplished: Boolean
)
