package sk.uxtweak.uxmobile.lifecycle

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import sk.uxtweak.uxmobile.UxMobile

class ApplicationLifecycleInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        UxMobile.initialize(context?.applicationContext as Application)
        ApplicationLifecycle.initialize(context?.applicationContext as Application)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun getType(uri: Uri): String? = null
}
