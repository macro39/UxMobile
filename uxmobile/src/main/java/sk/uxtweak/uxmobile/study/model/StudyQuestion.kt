package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyQuestion(

    @SerializedName("type")
    val type: String,

    @SerializedName("atTask")
    val atTask: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("answerType")
    val answerType: String,

    @SerializedName("answerOptions")
    val answerOptions: String,

    @SerializedName("required")
    val required: Boolean,

    @SerializedName("randomizeOptions")
    val randomizeOptions: Boolean
)
