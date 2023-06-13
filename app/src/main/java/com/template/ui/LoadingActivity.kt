package com.template.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.template.R
import com.template.ui.view_model.MainViewModel
import com.template.ui.view_model.MyViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoadingActivity : AppCompatActivity() {

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
        openChromeTabs()
    }

    private fun openChromeTabs() {

        with(viewModel) {

            if (isInternetAvailAble(this@LoadingActivity)) {

                coroutine.launch {

                    val linkExists = getLink()

                    if (isUrl(linkExists)) {

                        openLinkInChromeCustomTabs(linkExists)
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
    }
}