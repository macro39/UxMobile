package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by Kamil Macek on 22.2.2020.
 */
data class StudyQuestionnaire(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("instructions")
    val instructions: String,

    @JsonProperty("questions")
    val questions: List<StudyQuestion>
)
