package com.company.carryon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.company.carryon.data.network.initTokenStorage

class MainActivity : ComponentActivity() {

    private val permissionPrefs by lazy {
        getSharedPreferences("carryon_permissions", MODE_PRIVATE)
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    private val requestStartupPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTokenStorage(applicationContext)
        enableEdgeToEdge()

        setContent {
            App()
        }

        // Request runtime permissions after first frame scheduling to avoid
        // lifecycle timing issues during startup on some devices/ROMs.
        window.decorView.post {
            requestInitialPermissionsIfNeeded()
        }
    }

    private fun requestInitialPermissionsIfNeeded() {
        if (permissionPrefs.getBoolean(KEY_INITIAL_PERMISSIONS_REQUESTED, false)) return

        requestNotificationPermissionIfNeeded()

        val permissionsToRequest = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.CAMERA)
            add(Manifest.permission.READ_CONTACTS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        val missingPermissions = permissionsToRequest.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestStartupPermissions.launch(missingPermissions.toTypedArray())
        }

        permissionPrefs.edit()
            .putBoolean(KEY_INITIAL_PERMISSIONS_REQUESTED, true)
            .apply()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private companion object {
        const val KEY_INITIAL_PERMISSIONS_REQUESTED = "initial_permissions_requested"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
