package com.template.domain

class SaveLink(private val repository: Repository) {
    operator fun invoke(link: String) {
        repository.saveLink(link)
    }
}