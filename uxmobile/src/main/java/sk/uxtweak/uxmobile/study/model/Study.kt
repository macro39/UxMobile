package sk.uxtweak.uxmobile.study.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Kamil Macek on 6. 2. 2020.
 */
data class Study(

    @SerializedName("id")
    val studyId: Int = 0,

    @SerializedName("name")
    val name: String = "STUDY NAME",

    @SerializedName("closing_rule")
    val closingRule: String = "CLOSING RULE",

    @SerializedName("closing_date")
    val closingDate: Int = 0,

    @SerializedName("respondent_limit")
    val respondentLimit: Int = 0,

    @SerializedName("welcome_message")
    val welcomeMessage: String = "Welcome to this study, and thank you for agreeing to participate! The activity shouldn't take longer than 30 to 60 minutes to complete. Your response will help us to better understand how people behave in our app.",

    @SerializedName("introduction")
    val instruction: String = "<b>Here's how it works:</b><ol><li>You will be presented with a task.</li><li>After reading the task, you will be redirected to a website.</li><li>Click through the website as you naturally would in order to fulfill the task.</li><li>Once you arrive at the intended destination, click <b>Task done</b> and the task will end.</li><li>Repeat the previous steps for all the studyTasks to complete the RePlay study.</li></ol><em>This is not a test of your ability, there are no right or wrong answers.</em><br><b>That's it, let's get started!</b></div>",

    @SerializedName("pre_study_questionnaire")
    val preStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("tasks")
    val studyTasks: List<StudyTask> = arrayListOf(),

    @SerializedName("post_study_questionnaire")
    val postStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("thank_you_message")
    val thankYouMessage: String = "All done, awesome! Thanks again for your participation. Your feedback is incredibly useful in helping us understand how people interact with our app, so that we can make our application easier to use. You may now going back to your work!",

    @SerializedName("study_branding")
    val studyBranding: StudyBrandings = StudyBrandings()
)
