package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 16.3.2020.
 */
data class QuestionOption(

    @SerializedName("id")
    val id: Int,

    @SerializedName("option")
    val option: String
)
