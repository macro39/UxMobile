package sk.uxtweak.uxmobile.util

import android.util.Log

const val TAG = "UxMobile"

fun logd(tag: String, message: String) {
    val newMessage = "[${Thread.currentThread().name}] $message"
    Log.d(tag, newMessage)
}

fun logi(tag: String, message: String) {
    val newMessage = "[${Thread.currentThread().name}] $message"
    Log.i(tag, newMessage)
}

fun logw(tag: String, message: String, throwable: Throwable? = null) {
    val newMessage = "[${Thread.currentThread().name}] $message"
    if (throwable == null) Log.w(tag, newMessage) else Log.w(tag, newMessage, throwable)
}

fun loge(tag: String, message: String, throwable: Throwable? = null) {
    val newMessage = "[${Thread.currentThread().name}] $message"
    if (throwable == null) Log.e(tag, newMessage) else Log.e(tag, newMessage, throwable)
}
