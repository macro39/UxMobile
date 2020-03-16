package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 22.2.2020.
 */
data class StudyQuestionnaire(

    @SerializedName("name")
    val name: String,

    @SerializedName("instructions")
    val instructions: String,

    @SerializedName("questions")
    val questions: List<StudyQuestion>
)
