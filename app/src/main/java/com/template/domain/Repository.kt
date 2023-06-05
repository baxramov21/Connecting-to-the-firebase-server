package com.template.domain

interface Repository {

    suspend fun getLink(): Link
    suspend fun openLink(link: Link)
    suspend fun saveLink(link: Link)
    suspend fun clearDatabase()
    fun createLink(): Link
}