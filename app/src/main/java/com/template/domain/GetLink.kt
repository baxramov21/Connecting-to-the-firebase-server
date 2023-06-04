package com.template.domain

class GetLink(private val repository: Repository) {
    suspend fun getLink(): Link {
        return repository.getLink()
    }
}