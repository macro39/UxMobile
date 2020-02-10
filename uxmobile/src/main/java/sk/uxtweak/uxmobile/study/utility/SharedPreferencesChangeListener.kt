package sk.uxtweak.uxmobile.study.utility

import android.content.Context
import android.content.SharedPreferences
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptObserver

/**
 * Created by Kamil Macek on 13. 12. 2019.
 */
class SharedPreferencesChangeListener(
    context: Context,
    private val studyFlowAcceptObserver: StudyFlowAcceptObserver
) : SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferencesController(context) {

    private var isListenerAttached: Boolean = false

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key.equals(IN_STUDY_KEY)) {
            checkInStudyState()
        }
    }

    fun addListener() {
        if (!isListenerAttached) {
            isListenerAttached = true
            sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
//            checkInStudyState()
        }
    }

    fun removeListener() {
        if (isListenerAttached) {
            isListenerAttached = false
            sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        }
    }

    fun checkInStudyState() {
        if (sharedPreferences?.getBoolean(IN_STUDY_KEY, true)!!) {
            studyFlowAcceptObserver.studyAccepted(true)
        } else {
            studyFlowAcceptObserver.studyAccepted(false)
        }
    }
}
