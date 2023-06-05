package com.template.domain

class ClearDatabase(private val repository: Repository) {
    suspend fun clearDatabase() {
        repository.clearDatabase()
    }
}