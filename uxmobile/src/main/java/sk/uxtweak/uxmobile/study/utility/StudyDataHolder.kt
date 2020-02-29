package sk.uxtweak.uxmobile.study.utility

import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.model.QuestionnaireRules
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.model.StudyTask

/**
 * Created by Kamil Macek on 19.2.2020.
 */
object StudyDataHolder {
    var numberOfTasks: Int = 0
    var doingTaskWithName = ""
    lateinit var tasks: List<StudyTask>
    var study: Study? = null
    var questionnaireRules: QuestionnaireRules? = null
    var rejectMessage: StudyMessage = StudyMessage(Constants.REJECT_MESSAGE_TITLE, Constants.REJECT_MESSAGE_CONTENT)

    fun getBackgroundColorPrimary(): String {
        return study?.studyBrandings?.primaryColor!!
    }

    fun getBackgroundColorSecondary(): String {
        return study?.studyBrandings?.secondaryColor!!
    }

    fun setNewStudy(study: Study) {
        this.study = study
        this.tasks = study.studyTasks
        this.numberOfTasks = tasks.size
        this.rejectMessage = study.rejectMessage
    }
}
