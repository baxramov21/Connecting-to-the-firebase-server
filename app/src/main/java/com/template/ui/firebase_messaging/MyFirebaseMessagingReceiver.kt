package com.template.ui.firebase_messaging

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyFirebaseMessagingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, MyFirebaseMessagingService::class.java)
        intent.extras?.let { serviceIntent.putExtras(it) }
        context.startService(serviceIntent)
    }
}
