package com.template.data.main_code

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.template.data.converter.Mapper
import com.template.data.db.MainDatabase
import com.template.data.network.WebsiteTextCallback
import com.template.data.network.WebsiteTextDownloader
import com.template.domain.Link
import com.template.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.*


class RepositoryImpl(private val application: Application) : Repository {

    private val database = MainDatabase.newInstance(application).getDao()
    private val converter = Mapper()

    override suspend fun getLinkFromDatabase(): Link? {
        return converter.mapModelToEntity(database.getLink())
    }

    override suspend fun openLink(link: Link) {
        val intent = CustomTabsIntent.Builder()
            .build()
        intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.launchUrl(application, Uri.parse(link.link))
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
        Log.d("RepositoryImplementation", link)
        return Link(link)
    }

//    override suspend fun getLinkFromFirebase(): String {
//        val app = FirebaseApp.initializeApp(application)
//        val db = FirebaseFirestore.getInstance()
//        var linkValue = ""
//        val documentRef = db.collection("database").document("check")
//        documentRef.get()
//            .addOnSuccessListener { documentSnapshot ->
//                Log.d(TAG, "Starting on success listener")
//                if (documentSnapshot.exists()) {
//                    linkValue = documentSnapshot.getString("link").toString()
//                    Log.d(TAG, linkValue)
//                } else {
//                    linkValue = ERROR
//                    Log.d(TAG, "No such document!")
//                }
//
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Starting on failure listener")
//                linkValue = ERROR
//                Log.d(TAG, "Error getting document: $exception")
//            }
//        return linkValue
//    }

    override suspend fun getLinkFromFirebase(): String = withContext(Dispatchers.IO) {
        val app = FirebaseApp.initializeApp(application)
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("database").document("check")
        var linkValue = ERROR
        try {
            val documentSnapshot = documentRef.get().await()
            if (documentSnapshot.exists()) {
                linkValue = documentSnapshot.getString("link").toString()
            } else {
                linkValue = ERROR
                Log.d(TAG, "No such document!")
            }
        } catch (e: Exception) {
            linkValue = ERROR
            Log.d(TAG, "Error getting document: $e")
        }
        return@withContext linkValue
    }

    override suspend fun getLinkFromServer(serverLink: String): String {
        var result = ERROR

        val downloader = WebsiteTextDownloader(object : WebsiteTextCallback {
            override fun onWebsiteTextDownloaded(text: String?) {
                text?.let {
                    result = it
                }
            }
        })
        downloader.downloadWebsiteText(serverLink)
        return result
    }

    override suspend fun addHeader(link: Link): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val request = System.getProperty("http.agent")?.let {
            Request.Builder()
                .url(link.link)
                .header("User-Agent", it)
                .build()
        }

        val url = request?.url.toString()

        val response = request?.let { client.newCall(it).execute() }
        return@withContext url
    }

    companion object {
        private const val TAG = "RepositoryImpl"
        const val ERROR = "error"
    }
}