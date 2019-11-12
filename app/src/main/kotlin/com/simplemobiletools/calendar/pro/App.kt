package com.simplemobiletools.calendar.pro

import androidx.multidex.MultiDexApplication
import com.simplemobiletools.commons.extensions.checkUseEnglish
import sk.uxtweak.uxmobile.UxMobile

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()

        UxMobile.start("myapikey")
    }
}
