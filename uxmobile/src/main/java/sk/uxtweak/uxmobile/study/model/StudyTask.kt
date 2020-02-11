package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 28. 1. 2020.
 */
data class StudyTask(

    @SerializedName("taskId")
    val taskId: Long,

    @SerializedName("title")
    val title: String,

    var accomplished: Boolean = false
)
