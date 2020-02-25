package sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments

import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.QuestionOptionsFragment


/**
 * Created by Kamil Macek on 22.2.2020.
 */
abstract class FragmentQuestionnaireBase : Fragment() {

    lateinit var question: StudyQuestion
    lateinit var questionAnswers: ArrayList<String>

    fun configure() {
        question = QuestionOptionsFragment.currentQuestion
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
