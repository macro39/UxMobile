package sk.uxtweak.uxmobile.study

/**
 * Created by Kamil Macek on 14. 12. 2019.
 */
open class Constants {
    companion object Constants {
        const val LANGUAGE = "sk"   // or en
        const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 5469
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
