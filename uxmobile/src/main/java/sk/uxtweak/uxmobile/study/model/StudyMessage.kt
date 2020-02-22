package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyMessage(

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String
)
