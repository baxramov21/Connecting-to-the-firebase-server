package com.template.domain

class GetLinkFromFirebase(private val repository: Repository) {
    suspend fun getLinkFromFirebase(
        collectionName: String,
        documentName: String,
        fieldName: String
    ): String {
        return repository.getLinkFromFirebase(collectionName, documentName, fieldName)
    }
}