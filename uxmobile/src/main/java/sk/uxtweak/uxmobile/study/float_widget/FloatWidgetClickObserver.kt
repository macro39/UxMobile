package sk.uxtweak.uxmobile.study.float_widget

/**
 * Created by Kamil Macek on 24. 11. 2019.
 */
interface FloatWidgetClickObserver {
    fun studyStateChanged(studyInProgress: Boolean)
    fun instructionClicked(instructionClicked: Boolean)
}
