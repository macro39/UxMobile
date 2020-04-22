package sk.uxtweak.uxmobile.study

/**
 * Created by Kamil Macek on 14. 12. 2019.
 */
open class Constants {
    companion object Constants {
        var CONSENT_STRING =
            "<b>When UXMobile is integrated as a service we collect the following information:</b><ol><li>Consistent random pseudo ID for every end user</li><li>After reading the task, you will be redirected to a website.</li><li>Geographic location (Country only).</li><li>Screens visited by the end user in an app using UXMobile only.</li><li>Interaction patterns by the end user on your app (screen actions, gestures: taps, scrolls)</li><li>Information provided by the customer (except information customer chooses to block from being sent to UXMobile)</li></ol>We strictly adhere to a <b>non-recording policy of any PII</b> (Personally Identifiable Information) and contractually prohibits recording of sensitive information including ePHI, PCI or PII information. We provide API methods to hide PII and sensitive information.<br><br><b>We require you, as a customer, to hide PII information and only process anonymized data and to mention the usage of UXCam on your privacy policy.</b><br><br>As such the data UXMobile processes on behalf of its customers are anonymous.</div>"
        const val RECEIVER_IN_STUDY = "IN_STUDY"
        const val RECEIVER_STUDY_ENDED = "STUDY_ENDED"
        const val RECEIVER_STUDY_RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED =
            "RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED"
        const val RECEIVER_ASK_LATER = "LATER"
        const val EXTRA_INSTRUCTIONS_ONLY_ENABLED = "ONLY_INSTRUCTIONS"
        const val EXTRA_IS_STUDY_SET = "STUDY_IS_SET"
        const val EXTRA_END_OF_TASK = "END_OF_TASK"
        const val EXTRA_IS_SINGLE_LINE = "isSingleLine"
        const val EXTRA_IS_LIKERT_7 = "isLikert7"
        const val QUESTION_TYPE_INPUT = "single_line"
        const val QUESTION_TYPE_TEXT_AREA = "multi_line"
        const val QUESTION_TYPE_DROPDOWN = "dropdown_select"
        const val QUESTION_TYPE_RADIO_BUTTON = "radio_button"
        const val QUESTION_TYPE_CHECKBOX = "checkbox_select"
        const val QUESTION_TYPE_LIKERT_5 = "5_point_linker_scale"
        const val QUESTION_TYPE_LIKERT_7 = "7_point_linker_scale"
        const val QUESTION_TYPE_NET_PROMOTER = "net_promoter_score"
        const val REJECT_MESSAGE_TITLE = "Thank you and hope we see you next time!"
        const val REJECT_MESSAGE_CONTENT =
            "We are a little sad, but also next day is there a opportunity to change your favorite application by participating in study!"
        const val WELCOME_MESSAGE_TITLE = "Welcome!"
        const val INSTRUCTION_TITLE = "Instructions"
        const val THANK_YOU_MESSAGE_TITLE = "Thank you, that's all!"

        const val ADONIS_TOPIC = "plugin"
        const val ADONIS_EVENT_INITIALIZE = "initialize"
        const val ADONIS_EVENT_SEND_ANSWERS = "send_answers"
        const val ADONIS_EVENT_SEND_STUDY_ANSWERS = "send_study_answers"
        const val ADONIS_EVENT_QUIT = "quit"
        const val ADONIS_EVENT_SEND_QUESTIONNAIRE = "send_questionnaire"
        const val ADONIS_EVENT_SEND_STUDY = "send_study"

        const val MESSAGE_TYPE_CLOSING = "closing"
        const val MESSAGE_TYPE_INSTRUCTIONS = "instructions"
        const val MESSAGE_TYPE_WELCOME = "welcome"
    }
}
