package com.template.domain.usecase

import android.app.Activity
import com.template.domain.entity.MyResult
import com.template.domain.repository.Repository

class GetLFromF(private val repository: Repository) {
    suspend fun getLinkFromFirebase(
        fieldName: String,
        activity: Activity
    ): MyResult<String> {
        return repository.getLFromF(fieldName, activity)
    }
}