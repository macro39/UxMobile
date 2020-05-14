package sk.uxtweak.uxmobile.study.utility

import android.app.Application
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume


/**
 * Created by Kamil Macek on 23.4.2020.
 */
suspend fun getLocation(): String = suspendCancellableCoroutine { cont ->
    val client = OkHttpClient()
    val request = Request.Builder().method("GET", null).url("https://ipapi.co/json/").build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            cont.resume("ERROR")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseData = response.body()?.string()
            try {
                val json = JSONObject(responseData)
                cont.resume(json.getString("city") + " " + json.getString("country_name"))
            } catch (e: JSONException) {
                e.printStackTrace()
                cont.resume("ERROR")
            }
        }
    })
}

fun getIpAddress(application: Application): String {
    val wifiMgr: WifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo: WifiInfo = wifiMgr.connectionInfo
    val ip: Int = wifiInfo.ipAddress
    return Formatter.formatIpAddress(ip)
}

fun getOperatingSystem(): Int {
    return Build.VERSION.SDK_INT
}

fun getDeviceBrand(): String {
    return (Build.MANUFACTURER + " " + Build.MODEL)
}
