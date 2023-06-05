package com.template.domain

class CreateLink(private val repository: Repository) {
    fun createLink(url: String): Link {
        return repository.createLink()
    }
}