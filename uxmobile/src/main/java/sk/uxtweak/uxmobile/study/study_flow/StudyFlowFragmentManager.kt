package sk.uxtweak.uxmobile.study.study_flow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_study_flow.*
import sk.uxtweak.uxmobile.R
import sk.uxtweak.uxmobile.study.Constants
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_END_OF_TASK
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_INSTRUCTIONS_ONLY_ENABLED
import sk.uxtweak.uxmobile.study.Constants.Constants.EXTRA_IS_STUDY_SET
import sk.uxtweak.uxmobile.study.model.StudyMessage
import sk.uxtweak.uxmobile.study.study_flow.base.ConsentFragment
import sk.uxtweak.uxmobile.study.study_flow.base.GlobalMessageFragment
import sk.uxtweak.uxmobile.study.study_flow.messages.InstructionFragment
import sk.uxtweak.uxmobile.study.study_flow.messages.RejectedMessage
import sk.uxtweak.uxmobile.study.study_flow.messages.ThankYouMessage
import sk.uxtweak.uxmobile.study.study_flow.messages.WelcomeMessage
import sk.uxtweak.uxmobile.study.study_flow.questionnaire.PostStudyQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.questionnaire.PreStudyQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.questionnaire.ScreeningQuestionnaire
import sk.uxtweak.uxmobile.study.study_flow.task.TaskFragment
import sk.uxtweak.uxmobile.study.utility.ApplicationLanguageHelper
import sk.uxtweak.uxmobile.study.utility.StudyDataHolder


/**
 * Created by Kamil Macek on 1.2.2020.
 */
class StudyFlowFragmentManager : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        private var setColorFromStudyConfig = false
    }

    private val manager = supportFragmentManager

    private var isOnlyInstructionsDisplayed = false
    private var backNavEnabled = true
    private var rejectStudyBtnEnabled = true

    private var numberOfAvailableTasks = 0

    private var lastVisibleElement: View? = null

    var isScrolling = false

    private var isStudySet = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.appbar_title)
        setTheme(R.style.Theme_Base)
        setContentView(R.layout.activity_study_flow)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        numberOfAvailableTasks = StudyDataHolder.numberOfTasks

        // when content changed, check if can scroll and set action button to bottom visible
        scrollView_study_flow.viewTreeObserver.addOnGlobalLayoutListener {
            if (!isScrolling) {
                if (canEnableScroll()) {
                    if (canDisableSetActionButton()) {
                        action_button_to_bottom.visibility = View.VISIBLE
                    } else {
                        action_button_to_bottom.visibility = View.GONE
                    }
                } else {
                    action_button_to_bottom.visibility = View.GONE
                }
            }
        }

        // if user reach bottom with scroll, hide action button
        scrollView_study_flow.viewTreeObserver.addOnScrollChangedListener {
            isScrolling = true
            if (canDisableSetActionButton()) {
                action_button_to_bottom.visibility = View.VISIBLE
            } else {
                action_button_to_bottom.visibility = View.GONE
            }
        }

        action_button_to_bottom.setOnClickListener {
            scrollView_study_flow.smoothScrollTo(0, scrollView_study_flow.bottom)
        }

        if (savedInstanceState !== null) {

            if (setColorFromStudyConfig) {
                setColorFromConfig()
                return
            }

            return
        }

        if (intent.getBooleanExtra(EXTRA_IS_STUDY_SET, true)) {
            isStudySet = true

            if (intent.getBooleanExtra(EXTRA_END_OF_TASK, true)) {
                setColorFromConfig()
                onTaskCompletion()
                return
            }

            // check if fragments are only for purpose of instructions displayed - when doing task
            if (intent.getBooleanExtra(EXTRA_INSTRUCTIONS_ONLY_ENABLED, true)) {
                setColorFromConfig()
                isOnlyInstructionsDisplayed = true
                enableBackButton()
                showInstructions()
            } else {
                showConsent()
            }
        } else {
            isStudySet = false
            // only recording - consent, global message, recording without float button
            showConsent()
        }
    }

    /**
     * Set lang from constants
     */
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ApplicationLanguageHelper.wrap(newBase!!, Constants.LANGUAGE))
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

    private fun canEnableScroll(): Boolean {
        val displayHeight = scrollView_study_flow.height

        val realScrollHeight = scrollView_study_flow.getChildAt(0).height

        val exceededPixels = realScrollHeight - displayHeight

        val elementMarginBottom = lastVisibleElement!!.marginBottom
        val elementHeight = lastVisibleElement!!.height

        return (exceededPixels) > (elementHeight / 2 + elementMarginBottom)
    }

    private fun canDisableSetActionButton(): Boolean {
        val displayHeight = scrollView_study_flow.height

        val realScrollHeight = scrollView_study_flow.getChildAt(0).height

        val exceededPixels = realScrollHeight - displayHeight

        val offset = scrollView_study_flow.scrollY

        val elementMarginBottom = lastVisibleElement!!.marginBottom
        val elementHeight = lastVisibleElement!!.height

        return (exceededPixels - offset) > (elementHeight / 2 + elementMarginBottom)
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
        when (item.itemId) {
            R.id.button_reject_study_flow_action_bar -> {
                val builder = AlertDialog.Builder(this, R.style.DialogTheme)
                builder.setTitle(getString(R.string.plugin_name))
                builder.setMessage(getString(R.string.end_study))

                builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.cancel()
                }

                builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
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

    private fun showConsent() {
        if (StudyDataHolder.agreedWithTerms) {
            showGlobalMessage()
        } else {
            showFragment(ConsentFragment())
        }
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
//        showFragment(PostTaskQuestionnaire())
        // last task
        if (numberOfAvailableTasks == 0) {
            if (StudyDataHolder.study?.postStudyQuestionnaire != null && !StudyDataHolder.study?.postStudyQuestionnaire!!.questions.isNullOrEmpty()) {
                showFragment(PostStudyQuestionnaire())
            } else {
                showNextFragment(PostStudyQuestionnaire())
            }
        } else {
            disableEveryBackAction()
            showFragment(TaskFragment())
        }
    }

    fun setLastVisibleElement(view: View) {
        lastVisibleElement = view
    }

    /**
     * Default study flow starting fragment
     */
    private fun showGlobalMessage() {
        showFragment(GlobalMessageFragment())
    }

    /**
     * Change color based on configuration
     */
    private fun setColorFromConfig() {
        fragment_base_holder.setBackgroundColor(Color.parseColor(StudyDataHolder.getBackgroundColorPrimary()))                      // set background color from config
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(StudyDataHolder.getBackgroundColorSecondary())))     // set support bar color from config
    }

    /**
     * When user reject taking part in study in whichever part of study flow
     */
    fun showRejectedFragment() {
        this.disableEveryBackAction()
        showFragment(RejectedMessage())
    }

    fun isOnlyInstructionDisplayed(): Boolean {
        return isOnlyInstructionsDisplayed
    }

    /**
     * Show specific fragment by replacing old one
     */
    private fun showFragment(fragment: Fragment) {
        isScrolling = false

        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_base_holder, fragment)
        transaction.commit()
    }

    /**
     * Disable home button in support bar, back button pressed
     */
    private fun disableEveryBackAction() {
        this.backNavEnabled = false
        rejectStudyBtnEnabled = false
        this.invalidateOptionsMenu()
        this.enableBackButton()
    }

    /**
     * Controlling study flow based on specific requirements set by user
     */
    fun showNextFragment(actualFragment: Fragment) {
        when (actualFragment) {
            is ConsentFragment -> {
                StudyDataHolder.agreedWithTerms = true
                showGlobalMessage()
            }
            is GlobalMessageFragment -> {
                if (!isStudySet) {
                    startOnlyRecording()
                    return
                }
                if (StudyDataHolder.study != null) {
                    setColorFromStudyConfig = true
                    setColorFromConfig()
                    showFragment(WelcomeMessage())
                } else {
                    if (StudyDataHolder.screeningQuestionnaire != null && StudyDataHolder.screeningQuestionnaire!!.questions.isNotEmpty()) {
                        showFragment(ScreeningQuestionnaire())
                    } else {
                        showNextFragment(ScreeningQuestionnaire())
                    }
                }
            }
            is ScreeningQuestionnaire -> {
                if (StudyDataHolder.study?.welcomeMessage!!.isNotEmpty()) {
                    setColorFromStudyConfig = true
                    setColorFromConfig()
                    showFragment(WelcomeMessage())
                } else {
                    showNextFragment(WelcomeMessage())
                }
            }
            is WelcomeMessage -> {
                if (StudyDataHolder.study?.instruction!!.isNotEmpty()) {
                    showInstructions()
                } else {
                    showNextFragment(InstructionFragment())
                }
            }
            is InstructionFragment -> {
                if (isOnlyInstructionsDisplayed) {
                    onBackPressed()
                } else {
                    if (StudyDataHolder.study?.preStudyQuestionnaire != null && !StudyDataHolder.study?.preStudyQuestionnaire!!.questions.isNullOrEmpty()) {
                        showFragment(PreStudyQuestionnaire())
                    } else {
                        showNextFragment(PreStudyQuestionnaire())
                    }
                }
            }
            is PreStudyQuestionnaire -> {
                disableEveryBackAction()
                if (StudyDataHolder.tasks.isNotEmpty()) {
                    showFragment(TaskFragment())
                } else {
                    onTaskCompletion()
                }
            }
            is PostStudyQuestionnaire -> {
                disableEveryBackAction()
                showFragment(ThankYouMessage())
            }
            is ThankYouMessage -> {
                sendBroadcastStudyAccepted(accepted = false, ended = true)
                finish()
            }
        }
    }

    fun startOnlyRecording() {
        sendBroadcastStudyAccepted(accepted = true)
        finish()
    }

    fun studyAccepted(accepted: Boolean) {
        if (accepted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivityForResult(intent, Constants.CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                } else {
                    sendBroadcastStudyAccepted(accepted = true, ended = false)
                    finish()
                }
            } else {
                sendBroadcastStudyAccepted(accepted = true, ended = false)
                finish()
            }
        } else {
            sendBroadcastStudyAccepted(accepted = false, ended = true)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                sendBroadcastStudyAccepted(accepted = true, ended = false)
                finish()
            } else {
                showRejectedFragment()
            }
        }
    }

    fun askLater() {
        sendBroadcastStudyAccepted(later = true)
        finish()
    }

    private fun sendBroadcastStudyAccepted(
        accepted: Boolean = false,
        ended: Boolean = false,
        later: Boolean = false
    ) {
        val intent = Intent(Constants.RECEIVER_IN_STUDY)
        intent.putExtra(Constants.RECEIVER_IN_STUDY, accepted)
        intent.putExtra(Constants.RECEIVER_STUDY_ENDED, ended)
        intent.putExtra(
            Constants.RECEIVER_STUDY_RESUME_AFTER_ONLY_INSTRUCTIONS_ENABLED,
            isOnlyInstructionsDisplayed
        )
        intent.putExtra(Constants.RECEIVER_ASK_LATER, later)
        sendBroadcast(intent)
    }

    fun getData(actualFragment: Fragment): Any {
        when (actualFragment) {
            is ScreeningQuestionnaire -> {
                val data = StudyDataHolder.screeningQuestionnaire
                return data!!
            }
            is PreStudyQuestionnaire -> {
                return StudyDataHolder.study?.preStudyQuestionnaire!!
            }
            is PostStudyQuestionnaire -> {
                return StudyDataHolder.study?.postStudyQuestionnaire!!
            }
            is RejectedMessage -> {
                val message = StudyDataHolder.rejectMessage

                return if (message != null) {
                    StudyMessage(
                        getString(R.string.reject_title),
                        message
                    )
                } else {
                    StudyMessage(
                        getString(R.string.reject_title),
                        getString(R.string.reject_content)
                    )
                }
            }
            is WelcomeMessage -> {
                return StudyMessage(
                    getString(R.string.welcome) + " " + StudyDataHolder.study?.name,
                    StudyDataHolder.study?.welcomeMessage!!
                )
            }
            is InstructionFragment -> {
                return StudyMessage(
                    getString(R.string.instructions),
                    StudyDataHolder.study?.instruction!!
                )
            }
            is TaskFragment -> {
                return StudyDataHolder.tasks
            }
            is ThankYouMessage -> {
                return StudyMessage(
                    getString(R.string.thank_you),
                    StudyDataHolder.study?.thankYouMessage!!
                )
            }
        }
        return ""
    }
}
