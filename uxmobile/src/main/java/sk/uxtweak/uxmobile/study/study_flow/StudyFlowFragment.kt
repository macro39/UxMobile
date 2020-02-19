package sk.uxtweak.uxmobile.study.study_flow

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.utility.SharedPreferencesController
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder

class StudyFlowFragment : AppCompatActivity() {

    private val manager = supportFragmentManager
    private var sharedPreferencesController: SharedPreferencesController? = null
    private lateinit var permissionChecker: PermissionChecker

    private var isOnlyInstructionsDisplayed = false
    private var backNavEnabled = true

    private var numberOfAvailableTasks = 0

    // TODO should change this def value for study set
    private var isStudySet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_study_flow_base_fragment)

        numberOfAvailableTasks = StudyDataHolder.numberOfTasks
        sharedPreferencesController = SharedPreferencesController(this)
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
            finish()
        }
    }

    fun enableBackButton() {
        if (isOnlyInstructionsDisplayed || backNavEnabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isOnlyInstructionsDisplayed && backNavEnabled) {
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
                    //                    studyAccepted(false)
//                    finish()
                    showRejectedFragment()
                }

                builder.show()

                return true
            }
        }

        if (isOnlyInstructionsDisplayed) {
            super.onBackPressed()
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

    fun disableEveryBackAction() {
        this.backNavEnabled = false
        this.invalidateOptionsMenu()
        this.enableBackButton()
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
            is ScreeningQuestionnaireFragment -> showFragment(WelcomeMessageFragment())
            is WelcomeMessageFragment -> showInstructions()
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
            is ThankYouMessageFragment -> studyAccepted(false)
        }
    }

    fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            permissionChecker.canDrawOverlay()
            sharedPreferencesController?.changeInStudyState(true)

            finish()
        } else {
            sharedPreferencesController?.changeInStudyState(false)
            finish()
        }
    }

    fun askLater(later: Boolean) {
        // TODO add funcionality if user clicked later button
    }

    fun getData(actualFragment: Fragment): Any {
        when (actualFragment) {
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
}
