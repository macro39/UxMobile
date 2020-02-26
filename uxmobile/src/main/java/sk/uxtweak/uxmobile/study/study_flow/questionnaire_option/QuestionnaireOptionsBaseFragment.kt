package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.questionnaire.QuestionnaireBaseFragment


/**
 * Created by Kamil Macek on 22.2.2020.
 */
abstract class QuestionnaireOptionsBaseFragment : Fragment() {

    lateinit var question: StudyQuestion
    lateinit var questionAnswers: ArrayList<String>

    fun configure() {
        question = QuestionnaireBaseFragment.currentQuestion
        questionAnswers = arrayListOf()

        addOptions()
    }

    fun setAnswer(checkedId: Int) {
        questionAnswers = arrayListOf(question.questionOptions[checkedId])
    }

    fun addAnswer(checkedId: Int) {
        val answerToAdd = question.questionOptions[checkedId]
        if (!questionAnswers.contains(answerToAdd)) {
            questionAnswers.add(answerToAdd)
        }
    }

    fun removeAnswer(checkedId: Int) {
        val answerToAdd = question.questionOptions[checkedId]
        if (questionAnswers.contains(answerToAdd)) {
            questionAnswers.remove(answerToAdd)
        }
    }


    fun getAnswer(): QuestionAnswer {
        return QuestionAnswer(question.id, questionAnswers)
    }

    abstract fun addOptions()
}
