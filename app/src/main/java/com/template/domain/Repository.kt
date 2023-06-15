package com.template.domain

import android.app.Application

interface Repository {

    fun getLinkFromDatabase(application: Application): String?
    fun saveLink(link: String)
    fun createLink(domainFromFirebase: String): Link
    suspend fun getLinkFromFirebase(
        collectionName: String,
        documentName: String,
        fieldName: String
    ): String

    suspend fun getLinkFromServer(serverLink: String): String
}