package sk.uxtweak.uxmobile.study.study_flow.questionnaire_option

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.study.model.QuestionAnswer
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.questionnaire.QuestionnaireBaseFragment


/**
 * Created by Kamil Macek on 22.2.2020.
 */
abstract class QuestionnaireOptionsBaseFragment : Fragment() {

    lateinit var question: StudyQuestion
    private lateinit var questionAnswers: ArrayList<String>
    private var reason: String = ""
    private var reasonSet = false

    fun configure() {
        question = QuestionnaireBaseFragment.currentQuestion
        questionAnswers = arrayListOf()

        addOptions()
    }

    fun setText(text: String) {
        if (text.trim().isNotEmpty()) {
            questionAnswers = arrayListOf(text)
        }
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

    fun isQuestionAnsweredCorrectly(): Boolean {
        return if (question.answerRequired) {
            if (questionAnswers.isEmpty()) {
                showErrorToast(getString(sk.uxtweak.uxmobile.R.string.answer_empty))
                false
            } else {
                checkReasonNeeded()
            }
        } else {
            checkReasonNeeded()
        }
    }

    fun getAnswer(): QuestionAnswer {
        return QuestionAnswer(question.id, questionAnswers)
    }

    private fun checkReasonNeeded(): Boolean {
        return if (question.reasonNeeded) {
            return getReasonFromDialog(context)
        } else {
            true
        }
    }

    open fun getReasonFromDialog(context: Context?): Boolean {
        val handler: Handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(mesg: Message?) {
                throw RuntimeException()
            }
        }

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val dialog = AlertDialog.Builder(context)
            .setView(input)
            .setTitle(getString(sk.uxtweak.uxmobile.R.string.reason_title))
            .setPositiveButton(sk.uxtweak.uxmobile.R.string.next, null)
//            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                reason = input.text.toString()
                if (reason.trim().isNotEmpty()) {
                    reasonSet = true
                    handler.sendMessage(handler.obtainMessage())
                    dialog.dismiss()
                } else {
                    showErrorToast(getString(sk.uxtweak.uxmobile.R.string.reason_empty))
                }
            }
        }

        dialog.show()
        try {
            Looper.loop()
        } catch (e: RuntimeException) {
        }
        return reasonSet
    }


    private fun showErrorToast(message: String) {
        val toast: Toast =
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
//                toast.setGravity(Gravity.CENTER, 0, 0)
        val view: View = toast.view
        val textView: TextView = view.findViewById(R.id.message) as TextView
        textView.setTextColor(Color.RED)
        toast.show()
    }

    abstract fun addOptions()
}
