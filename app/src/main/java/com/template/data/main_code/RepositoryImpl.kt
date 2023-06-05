package com.template.data.main_code

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.template.data.converter.Mapper
import com.template.data.db.MainDatabase
import com.template.domain.Link
import com.template.domain.Repository

class RepositoryImpl(private val application: Application) : Repository {

    private val database = MainDatabase.newInstance(application).getDao()
    private val converter = Mapper()

    override suspend fun getLinkFromDatabase(): Link {
        return converter.mapModelToEntity(database.getLink())
    }

    override suspend fun openLink(link: Link) {
        TODO("Not yet implemented")
    }

    override suspend fun saveLink(link: Link) {
        database.addLink(
            converter.mapEntityToModel(link)
        )
    }

    override suspend fun clearDatabase() {
        database.clearTable()
    }

    override fun createLink(
        baseURL: String,
        packageName: String,
        userId: String,
        timeZone: String
    ): Link {
        val link =
            "$baseURL/?packageid=$packageName&usserid=$userId&getz=$timeZone&getr=utm_source=google-play&utm_medium=organic"
        return Link(link)
    }

    override suspend fun getLinkFromServer(): String {
        val db = FirebaseFirestore.getInstance()
        var linkValue = ""
        val documentRef = db.collection("yourCollectionName").document("yourDocumentId")
        documentRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    linkValue = documentSnapshot.getString("link").toString()
                } else {
                    linkValue = "No such document!"
                    println("No such document!")
                }
            }
            .addOnFailureListener { exception ->
                linkValue = "Error getting document: $exception"
                println("Error getting document: $exception")
            }
        return linkValue
    }
}