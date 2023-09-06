package com.template.domain.usecase

import com.template.domain.repository.Repository

class CreateL(private val repository: Repository) {
    fun createLink(
        domainFromFirebase: String,
        packageId: String,
        userID: String,
        timeZone: String
    ): String {
        return repository.createL(domainFromFirebase, packageId, userID, timeZone)
    }
}