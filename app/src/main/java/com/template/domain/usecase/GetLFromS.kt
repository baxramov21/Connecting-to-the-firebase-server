package com.template.domain.usecase

import com.template.domain.entity.MyResult
import com.template.domain.repository.Repository

class GetLFromS(private val repository: Repository) {
    suspend fun getLinkFromServer(urlSL: String): MyResult<String> {
        return repository.getLFromS(urlSL)
    }
}