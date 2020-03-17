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

    fun setText(text: String) {
        questionAnswers = arrayListOf(text)
    }

    fun setAnswer(checkedId: Int) {
        questionAnswers = arrayListOf(question.questionOptions[checkedId].id.toString())
    }

    fun addAnswer(checkedId: Int) {
        val answerToAdd = question.questionOptions[checkedId]
        if (!questionAnswers.contains(answerToAdd.id.toString())) {
            questionAnswers.add(answerToAdd.id.toString())
        }
    }

    fun removeAnswer(checkedId: Int) {
        val answerToAdd = question.questionOptions[checkedId]
        if (questionAnswers.contains(answerToAdd.id.toString())) {
            questionAnswers.remove(answerToAdd.id.toString())
        }
    }


    fun getAnswer(): QuestionAnswer {
        return QuestionAnswer(question.id, questionAnswers)
    }

    abstract fun addOptions()
}
