package com.template.domain.usecase

import com.template.domain.repository.Repository

class SaveL(private val repository: Repository) {
    operator fun invoke(link: String) {
        repository.saveLink(link)
    }
}