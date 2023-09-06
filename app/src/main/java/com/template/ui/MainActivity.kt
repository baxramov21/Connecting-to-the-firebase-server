package com.template.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.template.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        setContentView(R.layout.activity_main)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this, getString(R.string.notifications_disabled), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, getString(R.string.notifications_enabled), Toast.LENGTH_SHORT)
                    .show()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showAlertDialog(
                    "Показывать уведомления",
                    "Разрешить этому приложению показать уведомления",
                    "Разрешить"
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showAlertDialog(
        dialogTitle: String, dialogMessage: String, buttonTitle: String, action: () -> Unit
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)

        builder.setPositiveButton(buttonTitle) { dialog, which ->
            action()
        }

        builder.setNeutralButton(getString(R.string.no_thanks)) { dialog, which ->
            Toast.makeText(
                this,
                getString(R.string.permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val CHANNEL_ID = "my_channel_id"
        private const val PERMISSIONS_REQUEST_CODE = 123

        fun newInstance(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
