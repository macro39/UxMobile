package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by Kamil Macek on 16.3.2020.
 */
data class QuestionOption(

    @JsonProperty("id")
    val id: Int,

    @JsonProperty("option")
    val option: String
)
