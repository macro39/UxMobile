package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName
import sk.uxtweak.uxmobile.study.Constants


/**
 * Created by Kamil Macek on 21.2.2020.
 */
data class StudyQuestion(

    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("answer_type")
    val answerType: String = Constants.QUESTION_TYPE_INPUT,

    @SerializedName("answer_required")
    val answerRequired: Boolean = false,

    @SerializedName("reason_needed")
    val reasonNeeded: Boolean = false,

    @SerializedName("options")
    val questionOptions: ArrayList<QuestionOption> = arrayListOf()
)
