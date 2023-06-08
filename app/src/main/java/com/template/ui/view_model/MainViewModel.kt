package com.template.ui.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import com.template.data.main_code.RepositoryImpl
import com.template.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val application: Application) : ViewModel() {

    private val repository = RepositoryImpl(application)

    private val addHeader = AddHeader(repository)
    private val createLink = CreateLink(repository)
    private val getLinkFromDatabase = GetLinkFromDatabase(repository)
    private val getLinkFromFirebase = GetLinkFromFirebase(repository)
    private val openLink = OpenLink(repository)
    private val saveLink = SaveLink(repository)
    private val getLinkFromServer = GetLinkFromServer(repository)

    private val coroutine = CoroutineScope(Dispatchers.IO)


    fun openLinkIfExists(): Boolean {
        var result = false
        var mainUrl = ERROR

        coroutine.launch {
            val linkFromDatabase = PreferenceHelper.getResult(application)
            if (linkFromDatabase == null) {
                mainUrl = generateSafeLink()
                PreferenceHelper.saveResult(application, mainUrl)
            } else {
                mainUrl = PreferenceHelper.getResult(application) ?: ERROR
            }
            if (checkLink(mainUrl)) {
                val link = Link(mainUrl)
                openLink.openLink(link)
                result = true
            }
        }
        return result
    }

    private fun generateSafeLink(): String {
        var link = ""
        coroutine.launch {
            val linkFromFirebase = getLinkFromFirebase.getLinkFromFirebase()
            if (checkLink(linkFromFirebase)) {
                val siteUrl = createLink(linkFromFirebase)
                val recycledLink = addHeader.addHeader(Link(siteUrl))
                link = getLinkFromServer.getLinkFromServer(recycledLink)
            }
        }
        return link
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
    }
}