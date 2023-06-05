package com.template.domain

class GetLinkFromServer(private val repository: Repository) {
    suspend fun getLinkFromServer(): String {
        return repository.getLinkFromServer()
    }
}