package sk.uxtweak.uxmobile.study.study_flow.questionnaire

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowFragmentManager
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_option.*


/**
 * Created by Kamil Macek on 22.2.2020.
 */
open class QuestionnaireBaseFragment : Fragment() {

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
        (activity as StudyFlowFragmentManager).setLastVisibleElement(button_questionnaire_next)

        currentFragment = fragment

        textView_questionnaire_title.text = title
        textView_questionnaire_description.text = description

        questionsToAnswers = questions.toMutableList()
        totalQuestions = questions.size

        questionAnswers = arrayListOf() // initialize

        showNextQuestion()
    }

    fun nextOnClick(): Boolean {
        val childFragment =
            childFragmentManager.fragments.first() as QuestionnaireOptionsBaseFragment

        return if (childFragment.isQuestionAnsweredCorrectly()) {
            questionAnswers.addAll(childFragment.getAnswer())

            if (questionsToAnswers.size != 0) {
                showNextQuestion()
                true
            } else {
                false
            }
        } else {
            true
        }
    }

    private fun showNextQuestion() {
        (activity as StudyFlowFragmentManager).isScrolling = false


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
                    QuestionnaireOptionsDropdown()
                )
            }
            Constants.QUESTION_TYPE_RADIO_BUTTON -> {
                setQuestionTypeFragment(
                    QuestionnaireOptionsRadioButton()
                )
            }
            Constants.QUESTION_TYPE_CHECKBOX -> {
                setQuestionTypeFragment(
                    QuestionnaireOptionsCheckbox()
                )
            }
            Constants.QUESTION_TYPE_LIKERT_5 -> {
                setQuestionTypeLikert7(false)
            }
            Constants.QUESTION_TYPE_LIKERT_7 -> {
                setQuestionTypeLikert7(true)
            }
            Constants.QUESTION_TYPE_NET_PROMOTER -> {
                setQuestionTypeFragment(
                    QuestionnaireOptionsNetPromoter()
                )
            }
        }
    }

    private fun setQuestionTypeText(isSingleLine: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(Constants.EXTRA_IS_SINGLE_LINE, isSingleLine)

        val fragmentQuestionnaireOptionsText =
            QuestionnaireOptionsText()
        fragmentQuestionnaireOptionsText.arguments = bundle

        setQuestionTypeFragment(fragmentQuestionnaireOptionsText)
    }

    private fun setQuestionTypeLikert7(likert7: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(Constants.EXTRA_IS_LIKERT_7, likert7)

        val fragmentQuestionnaireOptionsLikert =
            QuestionnaireOptionsLikert()
        fragmentQuestionnaireOptionsLikert.arguments = bundle

        setQuestionTypeFragment(fragmentQuestionnaireOptionsLikert)
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
