package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class StudyQuestion(

    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("question_required")
    val questionRequired: Boolean,

    @SerializedName("description")
    val description: String,

    @SerializedName("answer_type")
    val answerType: String,

    @SerializedName("answer_required")
    val answerRequired: String,

    @SerializedName("reason_needed")
    val reasonNeeded: Boolean,

    @SerializedName("question_options")
    val questionOptions: Array<String>,

    @SerializedName("rule_values")
    val ruleValues: Array<String>
)
