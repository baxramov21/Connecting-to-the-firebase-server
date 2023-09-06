package com.template.ui.view_model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import com.template.data.main_code.RepositoryImpl
import com.template.domain.entity.MyResult
import com.template.domain.usecase.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val application: Application) : ViewModel() {

    private val repository = RepositoryImpl(application)
    private val createLink = CreateL(repository)
    private val getLinkFromDatabase = GetLFromD(repository)
    private val saveLink = SaveL(repository)
    private val getLinkFromFirebase = GetLFromF(repository)
    private val getLinkFromServer = GetLFromS(repository)

    private val coroutine = CoroutineScope(Dispatchers.IO)

    suspend fun getLink(activity: Activity, userID: String): String {
        var result = ERROR

        val mainUrl = coroutineScope {
            getLinkFromDatabase(application) ?: generateSafeLink(activity, userID)
        }

        if (checkServerResponse(mainUrl)) {
            result = mainUrl
            saveLink(mainUrl)
        }

        Log.d(TAG, "$mainUrl is link")
        return result
    }


    private suspend fun generateSafeLink(activity: Activity, userID: String): String {
        val event = CompletableDeferred<Unit>()
        var result = ERROR
        GlobalScope.launch {
            result = getConstructedLink(activity, userID)
            event.complete(Unit)
        }
        event.await()
        event.complete(Unit)
        return result
    }

    private suspend fun getConstructedLink(activity: Activity, userID: String): String =
        suspendCoroutine { continuation ->
            coroutine.launch {
                val domainFromFirebase = getLinkFromFirebase.getLinkFromFirebase(
                    fieldName,
                    activity
                )

                if (checkL(domainFromFirebase)) {
                    val packageId = application.packageName
                    val timeZone = TimeZone.getDefault().id
                    val complexLink =
                        createLink.createLink(
                            fetchLFromResultObject(domainFromFirebase),
                            packageId,
                            userID,
                            timeZone
                        )
                    val siteUrl = fetchLFromResultObject(
                        getLinkFromServer
                            .getLinkFromServer(complexLink)
                    )
                    continuation.resumeWith(Result.success(siteUrl))
                } else {
                    continuation.resumeWith(Result.success(ERROR))
                }
            }
        }

    fun isInternetAvailAble(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    }

    fun isUrl(string: String): Boolean {
        val urlRegex = "^(https?|ftp)://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}(:\\d{1,5})?(/.*)?$"
        val pattern = Regex(urlRegex)
        return pattern.matches(string)
    }

    private fun checkL(result: MyResult<String>): Boolean {
        return when (result) {
            is MyResult.Success -> true
            else -> false
        }
    }

    private fun fetchLFromResultObject(result: MyResult<String>): String {
        return when (result) {
            is MyResult.Success -> result.data
            is MyResult.BadResponseError -> ERROR
            is MyResult.NoValueError -> ERROR
        }
    }

    private fun checkServerResponse(l: String): Boolean {
        return l != ERROR
    }


    companion object {
        private const val ERROR = RepositoryImpl.ERROR
        private const val TAG = "MainViewModel"
        private const val fieldName = "check_link"
    }
}