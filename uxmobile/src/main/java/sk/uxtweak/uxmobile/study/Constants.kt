package sk.uxtweak.uxmobile.study

/**
 * Created by Kamil Macek on 14. 12. 2019.
 */
open class Constants {
    companion object Constants {
        const val RECEIVER_IN_STUDY = "IN_STUDY"
        const val RECEIVER_STUDY_ENDED = "STUDY_ENDED"
        const val RECEIVER_STUDY_RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED = "RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED"
        const val EXTRA_INSTRUCTIONS_ONLY_ENABLED = "ONLY_INSTRUCTIONS"
        const val EXTRA_IS_STUDY_SET = "STUDY_IS_SET"
        const val EXTRA_END_OF_TASK = "END_OF_TASK"
        const val EXTRA_IS_SINGLE_LINE = "isSingleLine"
        const val QUESTION_TYPE_INPUT = "input"
        const val QUESTION_TYPE_TEXT_AREA = "textarea"
        const val QUESTION_TYPE_DROPDOWN = "dropdown"
        const val QUESTION_TYPE_RADIO_BUTTON = "radiobtn"
        const val QUESTION_TYPE_CHECKBOX = "checkbox"
        const val REJECT_MESSAGE_TITLE = "DEFAULT REJECT MESSAGE"
        const val REJECT_MESSAGE_CONTENT = "We are a little sad, but also next day is there a opportunity to change your favorite application by participating in study!"
    }
}
