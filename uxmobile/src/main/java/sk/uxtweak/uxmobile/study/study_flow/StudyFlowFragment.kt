package sk.uxtweak.uxmobile.study.study_flow

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker
import sk.uxtweak.uxmobile.study.shared_preferences_utility.SharedPreferencesController

class StudyFlowFragment : AppCompatActivity() {

    private val manager = supportFragmentManager
    private var sharedPreferencesController: SharedPreferencesController? = null
    private lateinit var permissionChecker: PermissionChecker

    private var isOnlyInstructionsDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "UXMobile"
        setContentView(R.layout.activity_study_flow_fragment)

        sharedPreferencesController = SharedPreferencesController(this)
        permissionChecker = PermissionChecker(this)

        if (intent.getBooleanExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)) {
            showInstructions()
        } else {
            showGlobalMessage()
        }
    }

    /**
     * Avoid press back button when in study
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

    fun showInstructions() {
        showFragment(InstructionFragment(), R.id.fragment_base_holder)
    }

    fun showGlobalMessage() {
//        val transaction = manager.beginTransaction();
//        val globalMessageFragment = GlobalMessageFragment()
//        transaction.replace(R.id.fragment_base_holder, globalMessageFragment)
//        transaction.commit()
        showFragment(GlobalMessageFragment(), R.id.fragment_base_holder)
    }

    private fun showFragment(fragment: Fragment, id: Int) {
        val transaction = manager.beginTransaction()
        transaction.replace(id, fragment)
        transaction.commit()
    }

    fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            permissionChecker.canDrawOverlay()
            sharedPreferencesController?.changeInStudyState(true)

            // should call another
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
