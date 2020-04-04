package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Kamil Macek on 11. 2. 2020.
 */
data class StudyMessage(

    @JsonProperty("title")
    val title: String,

    @JsonProperty("content")
    val content: String
)
