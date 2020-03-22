package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class QuestionAnswer(

    @JsonProperty("id")
    val id: Int,

    @JsonProperty("answers")
    var answers: ArrayList<String>
)
