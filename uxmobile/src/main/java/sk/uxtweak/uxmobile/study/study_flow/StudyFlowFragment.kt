package sk.uxtweak.uxmobile.study.study_flow

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_study_flow.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.model.StudyQuestion
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_layouts.FragmentQuestionnaireOptionsRadioButton
import sk.uxtweak.uxmobile.study.study_flow.questionnaire_options_layouts.FragmentQuestionnaireOptionsText
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

class StudyFlowFragment : AppCompatActivity() {

    private val manager = supportFragmentManager
    private lateinit var permissionChecker: PermissionChecker

    private var isOnlyInstructionsDisplayed = false
    private var backNavEnabled = true
    private var rejectStudyBtnEnabled = true

    private var numberOfAvailableTasks = 0

    // TODO should change this def value for study set
    private var isStudySet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_study_flow)

        fragment_base_holder.setBackgroundColor(Color.parseColor(StudyDataHolder.getBackgroundColorPrimary()))                      // set background color from config
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(StudyDataHolder.getBackgroundColorSecondary())))     // set support bar color from config

        numberOfAvailableTasks = StudyDataHolder.numberOfTasks
        permissionChecker = PermissionChecker(this)

        if (intent.getBooleanExtra(EXTRA_IS_STUDY_SET, true)) {
            isStudySet = true

            if (intent.getBooleanExtra(EXTRA_END_OF_TASK, true)) {
                onTaskCompletion()
                return
            }

            // check if fragments are only for purpose of instructions displayed - when doing task
            if (intent.getBooleanExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)) {
                isOnlyInstructionsDisplayed = true
                enableBackButton()
                showInstructions()
            } else {
                showGlobalMessage()
            }
        } else {
            isStudySet = false
            // TODO only recording - global message, consent, recording without float button
        }
    }

    /**
     * Avoid press back button when in study flow
     */
    override fun onBackPressed() {
        if (isOnlyInstructionsDisplayed) {
            super.onBackPressed()
            sendBroadcastStudyAccepted(accepted = true, ended = false)
            finish()
        }
    }

    private fun enableBackButton() {
        if (isOnlyInstructionsDisplayed || backNavEnabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isOnlyInstructionsDisplayed && rejectStudyBtnEnabled) {
            menuInflater.inflate(R.layout.menu_study_flow, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.button_reject_study_flow_action_bar -> {
                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
                builder.setTitle("UXMOBILE")
                builder.setMessage("PRAJETE SI UKONCIT STUDIU?")

                builder.setNegativeButton("NIE") { dialog, which ->
                    dialog.cancel()
                }

                builder.setPositiveButton("ANO") { dialog, which ->
                    showRejectedFragment()
                }

                builder.show()

                return true
            }
        }

        if (isOnlyInstructionsDisplayed) {
            super.onBackPressed()
            sendBroadcastStudyAccepted(accepted = true, ended = false)
            finish()
            return true
        }

        return false
    }

    /**
     * Show instructions, enable/disable back button
     */
    private fun showInstructions() {
        showFragment(InstructionFragment())
    }

    /**
     * Show another task (task fragment), check if there is some task to be executed, otherwise show post study questionnaire
     */
    private fun onTaskCompletion() {
        showFragment(PostTaskQuestionnaire())
    }

    /**
     * Default study flow starting fragment
     */
    private fun showGlobalMessage() {
        showFragment(GlobalMessageFragment())
    }

    /**
     * When user reject taking part in study in whichever part of study flow
     */
    fun showRejectedFragment() {
        this.disableEveryBackAction()
        showFragment(RejectedMessageFragment())
    }

    /**
     * Show specific fragment by replacing old one
     */
    private fun showFragment(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_base_holder, fragment)
        transaction.commit()
    }

    private fun disableEveryBackAction() {
        this.backNavEnabled = false
        rejectStudyBtnEnabled = false
        this.invalidateOptionsMenu()
        this.enableBackButton()
    }

    private fun enableEveryBackAction() {
        rejectStudyBtnEnabled = true
        this.invalidateOptionsMenu()
    }

    /**
     * Controlling study flow based on specific requirements set by user
     */
    fun showNextFragment(actualFragment: Fragment) {
        // TODO add if statements, because not every fragment is required (345689 are optional)
        when (actualFragment) {
            is GlobalMessageFragment -> showFragment(ConsentFragment())
            is ConsentFragment -> {
                if (!isStudySet) {
                    studyAccepted(true)
                } else {
                    showFragment(ScreeningQuestionnaireFragment())
                }
            }
            is ScreeningQuestionnaireFragment -> {
                disableEveryBackAction()
                showFragment(WelcomeMessageFragment())
            }
            is WelcomeMessageFragment -> {
                enableEveryBackAction()
                showInstructions()
            }
            is InstructionFragment -> {
                if (isOnlyInstructionsDisplayed) {
                    onBackPressed()
                } else {
                    showFragment(PreStudyQuestionnaire())
                }
            }
            is PreStudyQuestionnaire -> showFragment(TaskFragment())
            is TaskFragment -> showFragment(PostStudyQuestionnaire())
            is PostTaskQuestionnaire -> {
                // last task
                if (numberOfAvailableTasks == 0) {
                    showFragment(PostStudyQuestionnaire())
                } else {
                    showFragment(TaskFragment())
                }
            }
            is PostStudyQuestionnaire -> {
                disableEveryBackAction()
                showFragment(ThankYouMessageFragment())
            }
            is ThankYouMessageFragment -> {
                sendBroadcastStudyAccepted(accepted = false, ended = true)
                finish()
            }
        }
    }

    fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            permissionChecker.canDrawOverlay()
            sendBroadcastStudyAccepted(accepted = true, ended = false)
            finish()
        } else {
            sendBroadcastStudyAccepted(accepted = false, ended = false)
            finish()
        }
    }

    private fun sendBroadcastStudyAccepted(accepted: Boolean, ended: Boolean) {
        val intent = Intent(Constants.RECEIVER_IN_STUDY)
        intent.putExtra(Constants.RECEIVER_IN_STUDY, accepted)
        intent.putExtra(Constants.RECEIVER_STUDY_ENDED, ended)
        intent.putExtra(
            Constants.RECEIVER_STUDY_RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED,
            isOnlyInstructionsDisplayed
        )
        sendBroadcast(intent)
    }

    fun askLater(later: Boolean) {
        // TODO add funcionality if user clicked later button
    }

    fun getData(actualFragment: Fragment): Any {
        when (actualFragment) {
            is ScreeningQuestionnaireFragment -> {
                val data = StudyDataHolder.getQuestionData(Constants.QUESTION_SCREENING)
                findProperQuestionType(actualFragment, data)
                return data
            }
            is PreStudyQuestionnaire -> {
                val data = StudyDataHolder.getQuestionData(Constants.QUESTION_PRE_STUDY)
                findProperQuestionType(actualFragment, data)
                return data
            }
            is PostTaskQuestionnaire -> {
                val data = StudyDataHolder.getQuestionData(Constants.QUESTION_TASK)
                findProperQuestionType(actualFragment, data)
                return data
            }
            is PostStudyQuestionnaire -> {
                val data = StudyDataHolder.getQuestionData(Constants.QUESTION_POST_STUDY)
                findProperQuestionType(actualFragment, data)
                return data
            }
            is RejectedMessageFragment -> {
                return StudyDataHolder.getMessageData(Constants.MESSAGE_REJECT)
            }
            is WelcomeMessageFragment -> {
                return StudyDataHolder.getMessageData(Constants.MESSAGE_WELCOME)
            }
            is InstructionFragment -> {
                return StudyDataHolder.getMessageData(Constants.INSTRUCTIONS)
            }
            is TaskFragment -> {
                return StudyDataHolder.tasks
            }
            is ThankYouMessageFragment -> {
                return StudyDataHolder.getMessageData(Constants.MESSAGE_COMPLETE)
            }
        }
        return ""
    }

    private fun findProperQuestionType(fragment: Fragment, studyQuestion: StudyQuestion) {
        when (studyQuestion.answerType) {
            Constants.QUESTION_TYPE_INPUT -> {
                setQuestionTypeText(fragment, true)
            }
            Constants.QUESTION_TYPE_TEXT_AREA -> {
                setQuestionTypeText(fragment, false)
            }
            Constants.QUESTION_TYPE_DROPDOWN -> {
                setQuestionTypeFragment(
                    fragment,
                    FragmentQuestionnaireOptionsRadioButton()
                )
            }
            Constants.QUESTION_TYPE_RADIO_BUTTON -> {
                setQuestionTypeFragment(
                    fragment,
                    FragmentQuestionnaireOptionsRadioButton()
                )
            }
            Constants.QUESTION_TYPE_CHECKBOX -> {
                setQuestionTypeFragment(
                    fragment,
                    FragmentQuestionnaireOptionsRadioButton()
                )
            }
        }
    }

    private fun setQuestionTypeText(fragment: Fragment, isSingleLine: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean(Constants.EXTRA_IS_SINGLE_LINE, isSingleLine)

        val fragmentQuestionnaireOptionsText = FragmentQuestionnaireOptionsText()
        fragmentQuestionnaireOptionsText.arguments = bundle

        setQuestionTypeFragment(fragment, fragmentQuestionnaireOptionsText)
    }

    private fun setQuestionTypeFragment(currentFragment: Fragment, newFragment: Fragment) {
        val transaction = currentFragment.childFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout_question_holder, newFragment)
        transaction.commit()
    }
}
