package com.template.domain

import android.app.Application

class GetLinkFromDatabase(private val repository: Repository) {
    operator fun invoke(application: Application): String? {
        return repository.getLinkFromDatabase(application)
    }
}