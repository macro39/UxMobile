package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.StudyFlowController
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.shared_preferences_utility.SharedPreferencesController
import java.util.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberProperties

class StudyFlowFragment : AppCompatActivity() {

    private val manager = supportFragmentManager
    private var sharedPreferencesController: SharedPreferencesController? = null
    private lateinit var permissionChecker: PermissionChecker

    private var isOnlyInstructionsDisplayed = false

    private var numberOfAvailableTasks = 0

    // TODO should change this def value for study set
    private var isStudySet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_study_flow_base_fragment)

//        numberOfAvailableTasks = StudyFlowController::class.companionObject?.memberProperties?.find {
//            it.name == "numberOfTasks"
//        }
        numberOfAvailableTasks = StudyFlowController.numberOfTasks
        Log.d("HAHA", "NUMBER OF TASKS: " + numberOfAvailableTasks)

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
        supportActionBar?.setDisplayHomeAsUpEnabled(isOnlyInstructionsDisplayed)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

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
     * Show specific fragment by replacing base fragment holder
     */
    private fun showFragment(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_base_holder, fragment)
        transaction.commit()
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
            is PostStudyQuestionnaire -> showFragment(ThankYouMessageFragment())
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
}
