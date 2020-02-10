package sk.uxtweak.uxmobile.study.utility


import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Kamil Macek on 13. 12. 2019.
 */
open class SharedPreferencesController(
    context: Context
) {

    companion object SharedPrefConstants {
        const val IN_STUDY_KEY = "IN_STUDY"
    }

    private val SHARED_PREFERENCES_NAME = "STUDY"

    var sharedPreferences : SharedPreferences? = null

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME , Context.MODE_PRIVATE)
    }


    fun changeInStudyState(inStudy: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("IN_STUDY", inStudy)
        editor?.apply()
    }

}
