package sk.uxtweak.uxmobile.util

import android.util.Log

const val TAG = "UxMobile"

fun logd(tag: String, message: String) = Log.d(tag, "[${Thread.currentThread().name}] $message")
fun logi(tag: String, message: String) = Log.i(tag, "[${Thread.currentThread().name}] $message")
fun logw(tag: String, message: String, throwable: Throwable? = null) =
    if (throwable == null) Log.w(tag, "[${Thread.currentThread().name}] $message") else Log.w(
        tag,
        "[${Thread.currentThread().name}] $message",
        throwable
    )

fun loge(tag: String, message: String, throwable: Throwable? = null) =
    if (throwable == null) Log.e(tag, "[${Thread.currentThread().name}] $message") else Log.e(
        tag,
        "[${Thread.currentThread().name}] $message",
        throwable
    )
