package com.example.carryon.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
actual fun rememberLocationRequester(
    onLocation: (lat: Double, lng: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchCurrentLocation(fusedClient, onLocation, onDenied)
        } else {
            onDenied()
        }
    }

    return remember(launcher, fusedClient) {
        {
            val fine = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarse = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (fine == PackageManager.PERMISSION_GRANTED ||
                coarse == PackageManager.PERMISSION_GRANTED
            ) {
                fetchCurrentLocation(fusedClient, onLocation, onDenied)
            } else {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}

@Suppress("MissingPermission")
private fun fetchCurrentLocation(
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocation: (Double, Double) -> Unit,
    onDenied: () -> Unit
) {
    // Try last known location first — fast and battery-free
    fusedClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocation(location.latitude, location.longitude)
        } else {
            // No cached location — request a fresh single fix
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
                .setMaxUpdates(1)
                .build()

            fusedClient.requestLocationUpdates(
                request,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        fusedClient.removeLocationUpdates(this)
                        val loc = result.lastLocation
                        if (loc != null) {
                            onLocation(loc.latitude, loc.longitude)
                        } else {
                            onDenied()
                        }
                    }
                },
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener {
        onDenied()
    }
}
