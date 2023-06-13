package com.template.ui

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.template.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseMessaging.getInstance()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (channel?.importance == NotificationManager.IMPORTANCE_NONE) {
                showNotificationPermissionExplanationDialog()
            }
        }
    }

    private fun showNotificationPermissionExplanationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Request")
            .setMessage("Этому приложению требуется разрешение на отправку уведомлений.")
            .setPositiveButton("Разрешить") { dialog, _ ->
                requestNotificationPermission()
                dialog.dismiss()
            }
            .setNegativeButton("Отклонить") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID)
            startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID)

                if (notificationChannel.importance != NotificationManager.IMPORTANCE_NONE) {
                  Toast.makeText(this, "Разрешено показывать уведомления", Toast.LENGTH_SHORT)
                        .show()
                } else {
                      Toast.makeText(this, "Отклонено показывать уведомления", Toast.LENGTH_SHORT)
                        .show()

                }

            }
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST = 123
        private const val CHANNEL_ID = "my_channel_id"

        fun newInstance(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

    }
}
