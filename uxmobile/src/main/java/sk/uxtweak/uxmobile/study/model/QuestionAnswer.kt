package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class QuestionAnswer(

    @SerializedName("questionId")
    val questionId: Int,

    @SerializedName("answer")
    var answer: String
)
