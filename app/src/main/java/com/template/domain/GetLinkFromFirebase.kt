package com.template.domain

class GetLinkFromFirebase(private val repository: Repository) {
    suspend fun getLinkFromFirebase(): String {
        return repository.getLinkFromFirebase()
    }
}