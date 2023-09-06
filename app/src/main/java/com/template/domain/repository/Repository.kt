package com.template.domain.repository

import android.app.Activity
import android.app.Application
import com.google.rpc.Help
import com.template.domain.entity.MyResult

interface Repository {

    fun getLFromD(application: Application): String?
    fun saveLink(link: String)
    fun createL(
        domainFromFirebase: String,
        packageId: String,
        userID: String,
        timeZone: String
    ): String

    suspend fun getLFromF(
        fieldName: String,
        activity: Activity
    ): MyResult<String>

    suspend fun getLFromS(urlSL: String): MyResult<String>
}