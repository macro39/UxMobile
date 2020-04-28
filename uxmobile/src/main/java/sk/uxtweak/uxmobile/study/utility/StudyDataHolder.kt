package sk.uxtweak.uxmobile.study.utility

import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.model.StudyQuestionnaire
import sk.uxtweak.uxmobile.study.model.StudyTask

/**
 * Created by Kamil Macek on 19.2.2020.
 */
object StudyDataHolder {
    var agreedWithTerms = false
    var numberOfTasks: Int = 0
    var doingTaskWithName = ""
    lateinit var tasks: List<StudyTask>
    var study: Study? = null
    var screeningQuestionnaire: StudyQuestionnaire? = null
    var rejectMessage: String? = null

    fun getBackgroundColorPrimary(): String {
        return study?.studyBranding?.primaryColor!!
    }

    fun getBackgroundColorSecondary(): String {
        return study?.studyBranding?.secondaryColor!!
    }

    fun setNewStudy(study: Study) {
        this.study = study
        this.tasks = study.studyTasks
        this.numberOfTasks = tasks.size

        for (studyMessage in this.study!!.messages) {
            when(studyMessage.type) {
                Constants.MESSAGE_TYPE_CLOSING -> this.study!!.thankYouMessage = studyMessage.text
                Constants.MESSAGE_TYPE_INSTRUCTIONS -> this.study!!.instruction = studyMessage.text
                Constants.MESSAGE_TYPE_WELCOME -> this.study!!.welcomeMessage = studyMessage.text
            }
        }
    }
}
