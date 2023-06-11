package com.template.data.main_code

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.template.R
import com.template.data.converter.Mapper
import com.template.data.db.MainDatabase
import com.template.data.network.WebsiteTextCallback
import com.template.data.network.WebsiteTextDownloader
import com.template.domain.Link
import com.template.domain.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class RepositoryImpl(private val application: Application) : Repository {

    private val database = MainDatabase.newInstance(application).getDao()
    private val converter = Mapper()

    override suspend fun getLinkFromDatabase(): Link? {
        return converter.mapModelToEntity(database.getLink())
    }

    override suspend fun openLink(link: Link) {
//        val intent = CustomTabsIntent.Builder()
//            .build()
//        intent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.launchUrl(application, Uri.parse(link.link))
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(application, R.color.black))
            .build()

        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.intent.setPackage("com.android.chrome") // Specify the package of the Chrome browser
        customTabsIntent.launchUrl(application, Uri.parse(link.link))
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

    override suspend fun getLinkFromFirebase(
        collectionName: String,
        documentName: String,
        fieldName: String
    ): String = withContext(Dispatchers.IO) {
        val app = FirebaseApp.initializeApp(application)
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection(collectionName).document(documentName)
        var linkValue = ERROR
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
        val client = OkHttpClient()
        var result: String = ERROR

        val request = Request.Builder()
            .url(serverLink)
            .header(
                "User-Agent",
                System.getProperty("http.agent")
            ) // Use the actual User-Agent header
            .build()

        try {
            val response = client.newCall(request).execute()

            val responseCode = response.code

            if (responseCode == 200) {
                result = response.body.toString()

            } else if (responseCode == 403) {
                result = ERROR
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    override suspend fun addHeader(link: Link) = withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(link.link)
            .header(
                "User-Agent",
                System.getProperty("http.agent")
            ) // Use the actual User-Agent header
            .build()

        try {
            val response = client.newCall(request).execute()
            // Handle the response...
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "RepositoryImpl"
        const val ERROR = "error"
    }
}