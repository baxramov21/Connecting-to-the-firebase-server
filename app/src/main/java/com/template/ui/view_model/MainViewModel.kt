package com.template.ui.view_model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.COLOR_SCHEME_DARK
import androidx.browser.customtabs.CustomTabsIntent.COLOR_SCHEME_LIGHT
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.template.R
import com.template.data.main_code.RepositoryImpl
import com.template.domain.*
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val application: Application) : ViewModel() {

    private val repository = RepositoryImpl(application)
    private val createLink = CreateLink(repository)
    private val getLinkFromDatabase = GetLinkFromDatabase(repository)
    private val saveLink = SaveLink(repository)
    private val getLinkFromFirebase = GetLinkFromFirebase(repository)
    private val getLinkFromServer = GetLinkFromServer(repository)

    private val coroutine = CoroutineScope(Dispatchers.IO)

    suspend fun getLink(): String {
        var result = ERROR

        val mainUrl = coroutineScope {
            getLinkFromDatabase(application) ?: generateSafeLink()
        }

        if (checkLink(mainUrl)) {
            result = mainUrl
            saveLink(mainUrl)
        }

        Log.d(TAG, "$mainUrl is link")
        return result
    }

    private suspend fun getConstructedLink(): String = suspendCoroutine { continuation ->
        coroutine.launch {
            val domainFromFirebase =
                getLinkFromFirebase.getLinkFromFirebase(collectionName, documentName, fieldName)
            if (checkLink(domainFromFirebase)) {
                val complexLink = createLink.createLink(domainFromFirebase)
                val siteUrl = getLinkFromServer.getLinkFromServer(complexLink.link)
                continuation.resumeWith(Result.success(siteUrl))
            } else {
                continuation.resumeWith(Result.success(""))
            }
        }
    }


    private suspend fun generateSafeLink(): String {
        val event = CompletableDeferred<Unit>()
        var result = ""
        GlobalScope.launch {
            result = getConstructedLink()
            event.complete(Unit)
        }
        event.await()
        event.complete(Unit)
        return result
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

    private fun checkLink(link: String): Boolean {
        return (link != ERROR && (link.isNotEmpty() && link.isNotBlank()))
    }

    fun openLinkInChromeCustomTabs(link: String, context: Context) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.black))
        builder.setColorScheme(COLOR_SCHEME_DARK)

        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(context, Uri.parse(link))
    }


    companion object {
        private const val ERROR = RepositoryImpl.ERROR
        private const val TAG = "MainViewModel"
        private const val collectionName = "database"
        private const val documentName = "check"
        private const val fieldName = "link"
    }
}