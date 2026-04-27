package com.company.carryon

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.company.carryon.data.network.initTokenStorage
import com.company.carryon.data.network.savePendingPushNavigation
import com.company.carryon.data.network.savePushToken
import com.company.carryon.data.network.PushNavigationSignal
import com.company.carryon.data.payment.StripePaymentLauncher
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTokenStorage(applicationContext)
        StripePaymentLauncher.init(this)
        enableEdgeToEdge()
        createNotificationChannel()
        handlePushNavigationIntent(intent)
        retrievePushToken()

        setContent {
            App()
        }

        // Request runtime permissions after first frame scheduling to avoid
        // lifecycle timing issues during startup on some devices/ROMs.
        window.decorView.post {
            requestNotificationPermissionIfNeeded()
            requestLocationPermissionIfNeeded()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePushNavigationIntent(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun requestLocationPermissionIfNeeded() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted) {
            requestLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                "carryon_notifications",
                "CarryOn Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Booking lifecycle notifications from CarryOn"
            }
            manager.createNotificationChannel(channel)
        }
    }

    private fun retrievePushToken() {
        val firebaseAvailable = try {
            FirebaseApp.getApps(this).isNotEmpty()
        } catch (_: Exception) {
            false
        }

        if (!firebaseAvailable) {
            Log.w("MainActivity", "Firebase is not configured. Skipping push token retrieval.")
            return
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.takeIf { it.isNotBlank() }?.let { token ->
                    savePushToken(token)
                    Log.d("MainActivity", "Push token retrieved")
                }
            } else {
                Log.w("MainActivity", "Failed to get push token", task.exception)
            }
        }
    }

    private fun handlePushNavigationIntent(intent: Intent?) {
        val pushType = intent?.getStringExtra(EXTRA_PUSH_TYPE)?.trim().orEmpty()
        if (pushType.isEmpty()) return

        val bookingId = intent?.getStringExtra(EXTRA_BOOKING_ID)?.trim()?.takeIf { it.isNotBlank() }
        val targetScreen = intent?.getStringExtra(EXTRA_TARGET_SCREEN)?.trim()?.takeIf { it.isNotBlank() }
        savePendingPushNavigation(
            type = pushType,
            bookingId = bookingId,
            targetScreen = targetScreen
        )
        PushNavigationSignal.signalPendingNavigation()
    }

    private companion object {
        const val EXTRA_PUSH_TYPE = "push_type"
        const val EXTRA_BOOKING_ID = "booking_id"
        const val EXTRA_TARGET_SCREEN = "target_screen"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
