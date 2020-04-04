package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class StudyQuestion(

    @JsonProperty("id")
    val id: Int,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("question_required")
    val questionRequired: Boolean,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("answer_type")
    val answerType: String,

    @JsonProperty("answer_required")
    val answerRequired: Boolean,

    @JsonProperty("reason_needed")
    val reasonNeeded: Boolean,

    @JsonProperty("question_options")
    val questionOptions: ArrayList<QuestionOption> = arrayListOf()
)
