package com.template.domain

class OpenLink(private val repository: Repository) {
    suspend fun openLink(link: Link) {
        repository.openLink(link)
    }
}