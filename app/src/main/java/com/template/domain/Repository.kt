package com.template.domain

import android.app.Application

interface Repository {

    suspend fun getLinkFromDatabase(): Link
    suspend fun openLink(link: Link)
    suspend fun saveLink(link: Link)
    suspend fun clearDatabase()
    fun createLink(baseURL: String, packageName: String, userId: String, timeZone: String): Link
    suspend fun getLinkFromServer(): String
}