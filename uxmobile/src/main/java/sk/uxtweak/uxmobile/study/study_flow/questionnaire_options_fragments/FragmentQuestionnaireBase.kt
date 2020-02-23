package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments

import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.QuestionOptionsFragment


/**
 * Created by Kamil Macek on 22.2.2020.
 */
abstract class FragmentQuestionnaireBase : Fragment() {

    var question: StudyQuestion? = null

    fun configure() {
        question = QuestionOptionsFragment.currentQuestion

        addOptions()
    }

    fun updateQuestionnaireAnswers(checkedId: Int) {
        QuestionOptionsFragment.addQuestionAnswer(
            QuestionAnswer(
                question!!.id,
                listOf(question!!.questionOptions[checkedId]).toTypedArray()
            )
        )
    }

    abstract fun addOptions()
}
