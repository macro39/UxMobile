package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_base_questionaire.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments.FragmentQuestionnaireOptionsCheckbox
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments.FragmentQuestionnaireOptionsDropdown
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments.FragmentQuestionnaireOptionsRadioButton
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_fragments.FragmentQuestionnaireOptionsText


/**
 * Created by Kamil Macek on 22.2.2020.
 */
open class QuestionOptionsFragment : Fragment() {

    companion object {
        lateinit var currentQuestion: StudyQuestion
        lateinit var questionAnswers: ArrayList<QuestionAnswer>

        fun addQuestionAnswer(newQuestionAnswer: QuestionAnswer) {
            if (questionAnswers.filter {
                    it.id == newQuestionAnswer.id
                }.any()) {
                questionAnswers.find { it.id == newQuestionAnswer.id }?.answers =
                    newQuestionAnswer.answers
            } else {
                questionAnswers.add(newQuestionAnswer)
            }
        }
    }

    lateinit var questionsToAnswers: MutableList<StudyQuestion>
    var answeringQuestionNo = 0
    var totalQuestions = 0

    private lateinit var currentFragment: Fragment

    private var getDataFromEditText = false
    private var getDataFromCheckBoxes = false

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
        return if (questionsToAnswers.size != 0) {
            showNextQuestion()
            true
        } else {
            if (getDataFromEditText) {
                val childFragment =
                    childFragmentManager.fragments.first() as FragmentQuestionnaireOptionsText
                addQuestionAnswer(
                    QuestionAnswer(
                        currentQuestion.id,
                        listOf(childFragment.getTextFromInput()).toTypedArray()
                    )
                )
            }

            if (getDataFromCheckBoxes) {
                val childFragment =
                    childFragmentManager.fragments.first() as FragmentQuestionnaireOptionsCheckbox
                addQuestionAnswer(
                    QuestionAnswer(
                        currentQuestion.id,
                        childFragment.getData().toTypedArray()
                    )
                )
            }

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

        getDataFromEditText = false
        getDataFromCheckBoxes = false

        findProperQuestionType(question.answerType)
    }

    private fun findProperQuestionType(answerType: String) {
        when (answerType) {
            Constants.QUESTION_TYPE_INPUT -> {
                getDataFromEditText = true
                setQuestionTypeText(true)
            }
            Constants.QUESTION_TYPE_TEXT_AREA -> {
                getDataFromEditText = true
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
                getDataFromCheckBoxes = true
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
