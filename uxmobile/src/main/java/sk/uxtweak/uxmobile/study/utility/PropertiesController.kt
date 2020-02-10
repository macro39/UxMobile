package sk.uxtweak.uxmobile.study.utility

import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.*

/**
 * Created by Kamil Macek on 10. 2. 2020.
 */
object PropertiesController {

    private val TAG = this::class.java.simpleName

    fun getBaseURL(context: Context): String? {
        return getProperty("base.url", context)
    }

    fun getEndpoint(key: String, context: Context): String? {
        return getProperty(key, context)
    }

    private fun getProperty(key: String, context: Context): String? {
        try {
            val properties = Properties()
            val assetManager = context.assets
            val inputStream =assetManager.open("url.properties")
            properties.load(inputStream)

            return properties.getProperty(key)
        } catch (e: IOException) {
            Log.e(TAG, "CANT LOAD url.properties FILE")
            return null
        }
    }
}
