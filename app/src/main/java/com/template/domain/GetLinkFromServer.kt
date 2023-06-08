package com.template.domain

import com.template.data.main_code.RepositoryImpl

class GetLinkFromServer(private val repository: Repository) {
    suspend fun getLinkFromServer(serverLink: String): String {
        return repository.getLinkFromServer(serverLink)
    }
}