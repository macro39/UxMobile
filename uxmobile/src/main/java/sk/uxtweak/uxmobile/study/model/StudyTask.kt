package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 28. 1. 2020.
 */
data class StudyTask(

    @SerializedName("id")
    val taskId: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = "",

    var accomplished: Boolean = false,
    var endedSuccessful: Boolean = false
)
