package sk.uxtweak.uxmobile.study.utility

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import java.util.*


/**
 * Created by Kamil Macek on 24.4.2020.
 */
class LocalizedContextWrapper(base: Context?) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, locale: Locale): ContextWrapper {
            var context: Context = context
            val configuration: Configuration = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale)
                context = context.createConfigurationContext(configuration)
            } else {
                configuration.locale = locale
                context.resources.updateConfiguration(
                    configuration,
                    context.resources.displayMetrics
                )
            }
            return LocalizedContextWrapper(context)
        }
    }
}
