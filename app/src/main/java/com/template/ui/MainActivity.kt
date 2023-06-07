package com.template.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.FirebaseApp
import com.template.R
import com.template.ui.view_model.MainViewModel
import com.template.ui.view_model.MyViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModelFactory by lazy {
        MyViewModelFactory(application)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        val linkExists = viewModel.openLinkIfExists()
        if (!linkExists) {
            // Link doesn't exist
        }
    }
}