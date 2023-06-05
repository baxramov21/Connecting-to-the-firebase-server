package com.template.domain

class CreateLink(private val repository: Repository) {
    fun createLink(baseURL: String, packageName: String, userId: String, timeZone: String): Link {
        return repository.createLink(baseURL, packageName, userId, timeZone)
    }
}