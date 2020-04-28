package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 23.4.2020.
 */
data class StudyMessageJson(

    @SerializedName("type")
    val type: String = "",

    @SerializedName("text")
    val text: String = ""
)
