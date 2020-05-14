package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 6. 2. 2020.
 */
data class Study(

    @SerializedName("id")
    val studyId: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("pre_study_questionnaire")
    val preStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("tasks")
    val studyTasks: List<StudyTask> = arrayListOf(),

    @SerializedName("messages")
    val messages: List<StudyMessageJson> = arrayListOf(),

    @SerializedName("post_study_questionnaire")
    val postStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("study_branding")
    val studyBranding: StudyBrandings = StudyBrandings(),

    var welcomeMessage: String = "",
    var instruction: String = "",
    var thankYouMessage: String = ""
)
