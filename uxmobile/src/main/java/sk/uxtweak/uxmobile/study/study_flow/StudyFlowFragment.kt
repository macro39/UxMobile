package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.shared_preferences_utility.SharedPreferencesController

class StudyFlowFragment : AppCompatActivity() {

    private val manager = supportFragmentManager
    private var sharedPreferencesController: SharedPreferencesController? = null
    private lateinit var permissionChecker: PermissionChecker

    private var isOnlyInstructionsDisplayed = false

    // TODO should change this def value for study set
    private var isStudySet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_study_flow_base_fragment)

        sharedPreferencesController = SharedPreferencesController(this)
        permissionChecker = PermissionChecker(this)

        if (intent.getBooleanExtra(EXTRA_IS_STUDY_SET, true)) {
            isStudySet = true
            // check if fragments are only for purpose of instructions displayed - when doing task
            if (intent.getBooleanExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)) {
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

    fun enableBackButton(enable: Boolean) {
        isOnlyInstructionsDisplayed = enable
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
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
     * When only instructions has to be shown in case of performing task
     */
    private fun showInstructions() {
        showFragment(InstructionFragment())
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
    fun showNextFragment(actualFragment : Fragment) {
        if (actualFragment is GlobalMessageFragment) {
            showFragment(ConsentFragment())
        }
        if (actualFragment is ConsentFragment) {
            // if only recording is enabled
            if (!isStudySet) {
                studyAccepted(true)
            } else {
                studyAccepted(true)
            }
        }
    }

    fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            permissionChecker.canDrawOverlay()
            sharedPreferencesController?.changeInStudyState(true)

            // should call another fragment
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
