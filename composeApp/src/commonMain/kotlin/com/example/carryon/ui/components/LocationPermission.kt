package com.example.carryon.ui.components

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that, when invoked, will:
 *  1. Request ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION permission if not yet granted
 *  2. Once granted (or already granted), fetch the device's current coordinates
 *  3. Call [onLocation] with (lat, lng) on success, or [onDenied] if refused
 */
@Composable
expect fun rememberLocationRequester(
    onLocation: (lat: Double, lng: Double) -> Unit,
    onDenied: () -> Unit = {}
): () -> Unit
