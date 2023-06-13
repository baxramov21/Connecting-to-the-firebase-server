package com.template.data.main_code

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.template.data.db.PreferenceHelper
import com.template.domain.Link
import com.template.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class RepositoryImpl(private val application: Application) : Repository {

    private val database = PreferenceHelper

    override fun getLinkFromDatabase(application: Application): String? {
        return database.getUrl(application)
    }

    override fun saveLink(link: String) {
        database.saveUrl(application, link)
    }

    override fun createLink(domainFromFirebase: String): Link {
        val packageId = application.packageName
        val uuid: UUID = UUID.randomUUID()
        val userId: String = uuid.toString()
        val timeZone = TimeZone.getDefault().id

        val url = StringBuilder(domainFromFirebase)
            .append("/?")
            .append("packageid=")
            .append(packageId)
            .append("&")
            .append("userid=")
            .append(userId)
            .append("&")
            .append("getz=")
            .append(timeZone)
            .append("&")
            .append("getr=utm_source=google-play&utm_medium=organic")
        return Link(url.toString())
    }

    override suspend fun getLinkFromFirebase(
        collectionName: String,
        documentName: String,
        fieldName: String
    ): String = withContext(Dispatchers.IO) {
        FirebaseApp.initializeApp(application)
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection(collectionName).document(documentName)
        var linkValue: String
        try {
            val documentSnapshot = documentRef.get().await()
            if (documentSnapshot.exists()) {
                linkValue = documentSnapshot.getString(fieldName).toString()
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
        val url = URL(serverLink)

        val connection = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

        connection.setRequestProperty("User-Agent", USER_AGENT)

        return when (connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = withContext(Dispatchers.IO) {
                    reader.readLine()
                }
                withContext(Dispatchers.IO) {
                    reader.close()
                }
                response
            }
            HttpURLConnection.HTTP_FORBIDDEN -> "error"
            else -> "unknown"
        }
    }

    companion object {
        private const val TAG = "RepositoryImpl"
        const val ERROR = "error"
        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.210 Mobile Safari/537.36"

    }
}