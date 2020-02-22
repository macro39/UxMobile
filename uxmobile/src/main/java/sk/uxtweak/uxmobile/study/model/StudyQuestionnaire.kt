package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Kamil Macek on 22.2.2020.
 */
data class StudyQuestionnaire(

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("questions")
    val questions: List<StudyQuestion>
)
