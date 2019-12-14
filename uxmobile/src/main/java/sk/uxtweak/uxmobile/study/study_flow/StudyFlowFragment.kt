package sk.uxtweak.uxmobile.study.study_flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.SharedPreferencesController
import sk.uxtweak.uxmobile.study.float_widget.PermissionChecker

class StudyFlowFragment: AppCompatActivity() {

    private val manager = supportFragmentManager
    private var sharedPreferencesController : SharedPreferencesController? = null
    private lateinit var permissionChecker: PermissionChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_flow_fragment)

        sharedPreferencesController = SharedPreferencesController(this)
        permissionChecker = PermissionChecker(this)

        showGlobalMessage()
    }

    fun showGlobalMessage() {
        val transaction = manager.beginTransaction();
        val globalMessageFragment = GlobalMessageFragment()
        transaction.replace(R.id.fragment_base_holder, globalMessageFragment)
        transaction.commit()
    }

    fun studyAccepted(accepted : Boolean) {
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
