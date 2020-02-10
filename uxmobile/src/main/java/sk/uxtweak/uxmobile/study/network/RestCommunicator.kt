package sk.uxtweak.uxmobile.study.network

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import sk.uxtweak.uxmobile.study.model.Study
import sk.uxtweak.uxmobile.study.utility.PropertiesController
import java.io.IOException

/**
 * Created by Kamil Macek on 5. 2. 2020.
 */
class RestCommunicator(val context: Context) {

    private val TAG = this::class.java.simpleName

    var client = OkHttpClient()

    fun getStudy(then: ((Study?) -> Unit)) {
        val url = PropertiesController.getBaseURL(context) +
            PropertiesController.getEndpoint("endpoint.study", context) +
            "1"

        val request = Request.Builder()
            .url(url)
            .build()

        val client = client

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()

                Log.d(TAG, body)

                val gson = GsonBuilder().create()
                val res: Study = gson.fromJson(body, Study::class.java)
                then(res)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "CANT GET STUDY")
                then(null)
            }
        })
    }
}
