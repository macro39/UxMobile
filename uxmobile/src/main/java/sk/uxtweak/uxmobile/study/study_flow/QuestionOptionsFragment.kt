package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments.*


/**
 * Created by Kamil Macek on 22.2.2020.
 */
open class QuestionOptionsFragment : Fragment() {

    companion object {
        lateinit var currentQuestion: StudyQuestion
        lateinit var questionAnswers: ArrayList<QuestionAnswer>
    }

    private lateinit var questionsToAnswers: MutableList<StudyQuestion>
    private var answeringQuestionNo = 0
    private var totalQuestions = 0

    private lateinit var currentFragment: Fragment

    fun configure(
        title: String,
        description: String,
        questions: List<StudyQuestion>,
        fragment: Fragment
    ) {
        currentFragment = fragment

        textView_questionnaire_title.text = title
        textView_questionnaire_description.text = description

        questionsToAnswers = questions.toMutableList()
        totalQuestions = questions.size

        questionAnswers = arrayListOf() // initialize

        showNextQuestion()
    }

    fun nextOnClick(): Boolean {

        questionAnswers.add((childFragmentManager.fragments.first() as FragmentQuestionnaireBase).getAnswer())

        return if (questionsToAnswers.size != 0) {
            showNextQuestion()
            true
        } else {
            false
        }
    }

    private fun showNextQuestion() {
        answeringQuestionNo++
        updateQuestionIndicator()

        val question = questionsToAnswers.first()
        questionsToAnswers.removeAt(0)
        currentQuestion = question

        textView_question_description.text = question.description

        findProperQuestionType(question.answerType)
    }

    private fun findProperQuestionType(answerType: String) {
        when (answerType) {
            Constants.QUESTION_TYPE_INPUT -> {
                setQuestionTypeText(true)
            }
            Constants.QUESTION_TYPE_TEXT_AREA -> {
                setQuestionTypeText(false)
            }
            Constants.QUESTION_TYPE_DROPDOWN -> {
                setQuestionTypeFragment(
                    FragmentQuestionnaireOptionsDropdown()
                )
            }
            Constants.QUESTION_TYPE_RADIO_BUTTON -> {
                setQuestionTypeFragment(
                    FragmentQuestionnaireOptionsRadioButton()
                )
            }
            Constants.QUESTION_TYPE_CHECKBOX -> {
                setQuestionTypeFragment(
                    FragmentQuestionnaireOptionsCheckbox()
                )
            }
        }
    }

    private fun setQuestionTypeText(isSingleLine: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(Constants.EXTRA_IS_SINGLE_LINE, isSingleLine)

        val fragmentQuestionnaireOptionsText =
            FragmentQuestionnaireOptionsText()
        fragmentQuestionnaireOptionsText.arguments = bundle

        setQuestionTypeFragment(fragmentQuestionnaireOptionsText)
    }

    private fun setQuestionTypeFragment(newFragment: Fragment) {
        val transaction = currentFragment.childFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout_question_holder, newFragment)
        transaction.commit()
    }

    private fun updateQuestionIndicator() {
        textView_questionnaire_indicator.text =
            getString(R.string.question) + answeringQuestionNo + "/" + totalQuestions
    }

}
