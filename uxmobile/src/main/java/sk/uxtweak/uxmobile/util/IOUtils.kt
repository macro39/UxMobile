package sk.uxtweak.uxmobile.util

import android.content.Context
import java.io.File

object IOUtils {
    lateinit var filesDir: File

    fun initialize(context: Context) {
        filesDir = context.filesDir
    }
}
