package sk.uxtweak.uxmobile.study

import android.content.Context
import android.content.SharedPreferences
import sk.uxtweak.uxmobile.study.study_flow.StudyFlowAcceptedObserver

/**
 * Created by Kamil Macek on 13. 12. 2019.
 */
class SharedPreferenceChangeListener(
    context: Context,
    private val studyFlowAcceptedObserver: StudyFlowAcceptedObserver
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
            checkInStudyState()
        }
    }

    fun checkInStudyState() {
        if (sharedPreferences?.getBoolean(IN_STUDY_KEY, true)!!) {
            studyFlowAcceptedObserver.studyAccepted(true)
        } else {
            studyFlowAcceptedObserver.studyAccepted(false)
        }
    }

    fun removeListener() {
        if (isListenerAttached) {
            isListenerAttached = false
            sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        }
    }
}
