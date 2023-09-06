package com.template.data.main_code

import android.app.Activity
import android.app.Application
import android.util.Log
import android.webkit.WebSettings
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.template.data.db.PreferenceHelper
import com.template.domain.entity.MyResult
import com.template.domain.repository.Repository
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.HttpException
import java.net.URLEncoder


class RepositoryImpl(private val application: Application) : Repository {

    private val database = PreferenceHelper

    override fun getLFromD(application: Application): String? {
        return database.getUrl(application)
    }

    override fun saveLink(link: String) {
        database.saveUrl(application, link)
    }

    override fun createL(
        domainFromFirebase: String,
        packageId: String,
        userID: String,
        timeZone: String
    ): String {

        val value =
            "$domainFromFirebase/?packageid=${URLEncoder.encode(packageId, "UTF-8")}" +
                    "&userid=$userID" +
                    "&getz=${URLEncoder.encode(timeZone, "UTF-8")}" +
                    "&getr=utm_source=google-play&utm_medium=organic"

        Log.d(TAG, "User id is: $userID")
        Log.d(TAG, "Full link is: $value")
        return value
    }


    override suspend fun getLFromF(fieldName: String, activity: Activity): MyResult<String> {
        FirebaseApp.initializeApp(application)
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        try {
            val fetchTask = remoteConfig.fetch()
            Tasks.await(fetchTask)

            return if (fetchTask.isSuccessful) {
                val activateTask = remoteConfig.activate()
                Tasks.await(activateTask)
                val checkLink = remoteConfig.getString("check_link")
                MyResult.Success(checkLink)
            } else {
                Log.e(TAG, "No value in firebase available")
                MyResult.NoValueError("Fetch failed")
            }
        } catch (exception: Exception) {
            Log.e(TAG, "${exception.message}")
            return MyResult.NoValueError(exception.message ?: "Unknown Error")
        }
    }

    override suspend fun getLFromS(urlSL: String): MyResult<String> {
        val client = OkHttpClient()

        val userAgent = getUserAgent()

        val request = buildOkHttpRequest(urlSL, userAgent)

        Log.d(TAG, "$userAgent is user agent")
        if (request == null) {
            return MyResult.BadResponseError("Error Response: Access Forbidden")
        }
        val response = client.newCall(request).execute()
        val statusCode = response.code

        return if (statusCode == 200) {
            val textContent = response.body?.string()
            Log.d(TAG, "Server response: $textContent")
            MyResult.Success(textContent ?: ERROR)
        } else {
            Log.d(TAG, "Error Response: Access Forbidden")
            MyResult.BadResponseError(response.message ?: ERROR)
        }
    }

    private fun buildOkHttpRequest(urlSL: String, userAgent: String): Request? {
        return try {
            Request.Builder()
                .url(urlSL)
                .addHeader(
                    "User-Agent",
                    userAgent
                )
                .build()
        } catch (e: HttpException) {
            Log.e(TAG, "${e.message}")
            return null
        } catch (e: RuntimeException) {
            Log.e(TAG, "${e.message}")
            return null
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "${e.message}")
            return null
        }
    }

    private fun getUserAgent(): String = WebSettings.getDefaultUserAgent(application)

    companion object {
        private const val TAG = "RepositoryImpl"
        const val ERROR = "error"
    }
}