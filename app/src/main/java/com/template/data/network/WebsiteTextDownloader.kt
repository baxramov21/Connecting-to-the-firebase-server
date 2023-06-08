package com.template.data.network

import okhttp3.*
import java.io.IOException

class WebsiteTextDownloader(private val callback: WebsiteTextCallback) {

    fun downloadWebsiteText(url: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback.onWebsiteTextDownloaded(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                callback.onWebsiteTextDownloaded(body)
            }
        })
    }
}
