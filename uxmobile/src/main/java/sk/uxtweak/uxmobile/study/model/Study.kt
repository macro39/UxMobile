package sk.uxtweak.uxmobile.study.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Kamil Macek on 6. 2. 2020.
 */
data class Study(

    @JsonProperty("name")
    val name: String,

    @JsonProperty("id")
    val studyId: String,

    @JsonProperty("closing_rule")
    val closingRule: String,

    @JsonProperty("closing_date")
    val closingDate: Int,

    @JsonProperty("respondent_limit")
    val respondentLimit: Int,

    @JsonProperty("welcome_message")
    val welcomeMessage: String,

    @JsonProperty("introduction")
    val instruction: String,

    @JsonProperty("pre_study_questionnaire")
    val preStudyQuestionnaire: StudyQuestionnaire,

    @JsonProperty("task_list")
    val studyTasks: List<StudyTask>,

    @JsonProperty("post_study_questionnaire")
    val postStudyQuestionnaire: StudyQuestionnaire,

    @JsonProperty("thank_you_message")
    val thankYouMessage: String,

    @JsonProperty("study_branding")
    val studyBranding: StudyBrandings
)
