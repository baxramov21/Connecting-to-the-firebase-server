package com.template.ui.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.template.data.main_code.RepositoryImpl
import com.template.domain.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.suspendCoroutine

class MainViewModel(private val application: Application) : ViewModel() {

    private val repository = RepositoryImpl(application)

    private val addHeader = AddHeader(repository)
    private val createLink = CreateLink(repository)
    private val getLinkFromDatabase = GetLinkFromDatabase(repository)
    private val saveLink = SaveLink(repository)
    private val getLinkFromFirebase = GetLinkFromFirebase(repository)
    private val openLink = OpenLink(repository)
    private val getLinkFromServer = GetLinkFromServer(repository)

    private val coroutine = CoroutineScope(Dispatchers.IO)


    suspend fun openLinkIfExists(): Boolean {
        var result = false

        val mainUrl = coroutineScope {
            PreferenceHelper.getResult(application) ?: generateSafeLink()
        }

        if (checkLink(mainUrl)) {
            val link = Link(mainUrl)
            openLink.openLink(link)
            result = true
            PreferenceHelper.saveResult(application, mainUrl)
        }

        Log.d(TAG, "$mainUrl is link")

        return result
    }

    private suspend fun getCreatedLink(): String = suspendCoroutine { continuation ->
        coroutine.launch {
            val linkFromFirebase =
                getLinkFromFirebase.getLinkFromFirebase(collectionName, documentName, fieldName)
            if (checkLink(linkFromFirebase)) {
                val recycledLink = addHeader.addHeader(Link(linkFromFirebase))
                val siteUrl = getLinkFromServer.getLinkFromServer(recycledLink)
                continuation.resumeWith(Result.success(siteUrl)) // Return the result
            } else {
                continuation.resumeWith(Result.success("")) // Return empty string or handle other cases
            }
        }
    }


    private suspend fun generateSafeLink(): String {
        val event = CompletableDeferred<Unit>() // Create an instance of CompletableDeferred
        var result: String = ""
        // Start the code execution in the background
        GlobalScope.launch {
            result = getCreatedLink()
            event.complete(Unit)
        }

        // Wait for the event to be completed
        event.await()

        // Code execution is complete, continue with the coroutine
        println("Coroutine continues after code execution")

        // Perform other tasks...

        // Complete the event to release the waiting coroutine
        event.complete(Unit)
        return result
    }

    private fun createLink(baseUrl: String): String {
        val packageName = application.packageName
        val uuid: UUID = UUID.randomUUID()
        val userId: String = uuid.toString()
        val timeZone = TimeZone.getDefault().id

        val url = createLink.createLink(baseUrl, packageName, userId, timeZone)
        return url.link
    }

    private fun checkLink(link: String): Boolean {
        return (link != ERROR && (link.isNotEmpty() && link.isNotBlank()))
    }

    companion object {
        const val ERROR = RepositoryImpl.ERROR
        private const val TAG = "MainViewModel"

        private const val collectionName = "database"
        private const val documentName = "check"
        private const val fieldName = "link"
    }
}