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

    @SerializedName("closing_rule")
    val closingRule: String = "",

    @SerializedName("closing_date")
    val closingDate: Int = 0,

    @SerializedName("respondent_limit")
    val respondentLimit: Int = 0,

    @SerializedName("welcome_message")
    var welcomeMessage: String = "",
    // Welcome to this study, and thank you for agreeing to participate! The activity shouldn't take longer than 30 to 60 minutes to complete. Your response will help us to better understand how people behave in our app.

    @SerializedName("introduction")
    var instruction: String = "",
    // ### **Here's how it works:**
    //* You will be presented with a task.
    //* After reading the task, you will be redirected to a website.
    //* Click through the website as you naturally would in order to fulfill the task.
    //* Once you arrive at the intended destination, click Task done and the task will end.
    // * Repeat the previous steps for all the studyTasks to complete the RePlay study.
    //* This is not a test of your ability, there are no right or wrong answers.
    //
    //**That's it, let's get started!**

    @SerializedName("pre_study_questionnaire")
    val preStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("tasks")
    val studyTasks: List<StudyTask> = arrayListOf(),

    @SerializedName("messages")
    val messages: List<StudyMessageJson> = arrayListOf(),

    @SerializedName("post_study_questionnaire")
    val postStudyQuestionnaire: StudyQuestionnaire? = null,

    @SerializedName("thank_you_message")
    var thankYouMessage: String = "",
    // All done, awesome! Thanks again for your participation. Your feedback is incredibly useful in helping us understand how people interact with our app, so that we can make our application easier to use. You may now going back to your work!

    @SerializedName("study_branding")
    val studyBranding: StudyBrandings = StudyBrandings()
)
