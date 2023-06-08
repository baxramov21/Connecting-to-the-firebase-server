package com.template.domain

class AddHeader(private val repository: Repository) {
    suspend fun addHeader(link: Link): String {
        return repository.addHeader(link)
    }
}