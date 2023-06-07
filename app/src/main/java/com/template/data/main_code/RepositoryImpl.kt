package com.template.data.main_code

import android.app.Application
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.firestore.FirebaseFirestore
import com.template.data.converter.Mapper
import com.template.data.db.MainDatabase
import com.template.domain.Link
import com.template.domain.Repository
import okhttp3.OkHttpClient
import okhttp3.Request

class RepositoryImpl(private val application: Application) : Repository {

    private val database = MainDatabase.newInstance(application).getDao()
    private val converter = Mapper()

    override suspend fun getLinkFromDatabase(): Link {
        return converter.mapModelToEntity(database.getLink())
    }

    override suspend fun openLink(link: Link) {
        val client = OkHttpClient()

        val request = System.getProperty("http.agent")?.let {
            Request.Builder()
                .url(link.link)
                .header("User-Agent", it)
                .build()
        }

        val url = request?.url().toString()

        val response = request?.let { client.newCall(it).execute() }

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(application, Uri.parse(url))
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
//        val link =
//            "$baseURL/?packageid=$packageName&usserid=$userId&getz=$timeZone&getr=utm_source=google-play&utm_medium=organic"
//        return Link(link)
        return Link("https://www.youtube.com")
    }

    override suspend fun getLinkFromServer(): String {
        val db = FirebaseFirestore.getInstance()
        var linkValue = ""
        val documentRef = db.collection("database").document("check")
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
                linkValue = "Error getting document"
                println("Error getting document: $exception")
            }
        return linkValue
    }

    companion object {
        const val NO_DOC = "No such document!"
        const val ERROR_GETTING_DOC = "Error getting document"
    }
}