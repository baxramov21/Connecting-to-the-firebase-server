package com.template.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        var linkExists = false
        coroutine.launch {
            linkExists = viewModel.openLinkIfExists()
        }
        if (!linkExists) {
            // Link doesn't exist
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}