package com.template.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import com.template.data.main_code.RepositoryImpl
import com.template.domain.CreateLink
import java.util.*

class MainViewModel(private val application: Application) : ViewModel() {

    private val repository = RepositoryImpl(application)
    private val createLink = CreateLink(repository)

    fun createLink() {
        val baseUrl = ""
        val packageName = application.packageName
        val uuid: UUID = UUID.randomUUID()
        val userId: String = uuid.toString()
        val timeZone = TimeZone.getDefault().id
        createLink.createLink(baseUrl, packageName, userId, timeZone)
    }
}