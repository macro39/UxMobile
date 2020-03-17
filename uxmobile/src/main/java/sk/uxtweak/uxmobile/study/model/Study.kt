package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 6. 2. 2020.
 */
data class Study(

    @SerializedName("name")
    val name: String,

    @SerializedName("id")
    val studyId: String,

    @SerializedName("closing_rule")
    val closingRule: String,

    @SerializedName("closing_date")
    val closingDate: Int,

    @SerializedName("respondent_limit")
    val respondentLimit: Int,

    @SerializedName("welcome_message")
    val welcomeMessage: String,

    @SerializedName("introduction")
    val instruction: String,

    @SerializedName("pre_study_questionnaire")
    val preStudyQuestionnaire: StudyQuestionnaire,

    @SerializedName("task_list")
    val studyTasks: List<StudyTask>,

    @SerializedName("post_study_questionnaire")
    val postStudyQuestionnaire: StudyQuestionnaire,

    @SerializedName("thank_you_message")
    val thankYouMessage: String,

    @SerializedName("study_branding")
    val studyBranding: StudyBrandings
)
