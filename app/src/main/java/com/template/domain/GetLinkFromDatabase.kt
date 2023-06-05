package com.template.domain

class GetLinkFromDatabase(private val repository: Repository) {
    suspend fun getLinkFromDatabase(): Link {
        return repository.getLinkFromDatabase()
    }
}