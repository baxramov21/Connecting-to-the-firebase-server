package com.template.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.template.R
import com.template.ui.view_model.MainViewModel
import com.template.ui.view_model.MyViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LoadingActivity : AppCompatActivity() {

    private lateinit var analytics: FirebaseAnalytics

    private val viewModelFactory by lazy {
        MyViewModelFactory(application)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val coroutine = CoroutineScope(Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        FirebaseApp.initializeApp(this)
        analytics = Firebase.analytics
        openChromeTabs()
    }

    private fun openChromeTabs() {
        val userID = UUID.randomUUID().toString()

        with(viewModel) {
            if (isInternetAvailAble(this@LoadingActivity)) {
                coroutine.launch {
                    val linkExists = getLink(this@LoadingActivity, userID)
                    if (isUrl(linkExists)) {
                        openLinkInChromeCustomTabs(linkExists, this@LoadingActivity)
                        finish()
                    } else {
                        startActivity(MainActivity.newInstance(this@LoadingActivity))
                    }
                }

            } else {
                startActivity(MainActivity.newInstance(this@LoadingActivity))
                Toast.makeText(
                    this@LoadingActivity,
                    getString(R.string.check_internet_connection),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

//        coroutine.launch {
//            val linkExists = viewModel.getLink(this@LoadingActivity, userID)
//        }
    }

    private fun openLinkInChromeCustomTabs(link: String, context: Context) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.black))
        builder.setColorScheme(CustomTabsIntent.COLOR_SCHEME_DARK)

        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(context, Uri.parse(link))
    }
}