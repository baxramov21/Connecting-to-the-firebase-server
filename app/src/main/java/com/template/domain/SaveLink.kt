package com.template.domain

class SaveLink(private val repository: Repository) {
    suspend fun saveLink(link: Link) {
        repository.saveLink(link)
    }
}