package com.template.domain

class CreateLink(private val repository: Repository) {
    fun createLink(domainFromFirebase: String): Link {
        return repository.createLink(domainFromFirebase)
    }
}