package com.template.domain.usecase

import android.app.Application
import com.template.domain.repository.Repository

class GetLFromD(private val repository: Repository) {
    operator fun invoke(application: Application): String? {
        return repository.getLFromD(application)
    }
}