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

    private val createLink = CreateLink(repository)
    private val getLinkFromDatabase = GetLinkFromDatabase(repository)
    private val getLinkFromServer = GetLinkFromServer(repository)
    private val openLink = OpenLink(repository)
    private val saveLink = SaveLink(repository)

    private val coroutine = CoroutineScope(Dispatchers.IO)

    fun openLinkIfExists(): Boolean {
        var result = false
        coroutine.launch {
            val url = generateSafeLink()
            if (url != ERROR) {
                val link = Link(url)
                openLink.openLink(link)
                saveLink.saveLink(link)
                result = true
            }
        }
        return result
    }

    private fun generateSafeLink(): String {
        var validURL = true
        var link = ""

        coroutine.launch {
            link = getLinkFromServer.getLinkFromServer()
            validURL = checkLink(link)
        }

        return if (validURL) {
            createLink(link)
        } else {
            ERROR
        }
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
        var result = true
        coroutine.launch {
            result = !(link == RepositoryImpl.NO_DOC
                    && link == RepositoryImpl.ERROR_GETTING_DOC)
        }
        return result
    }

    companion object {
        const val ERROR = "error"
    }
}