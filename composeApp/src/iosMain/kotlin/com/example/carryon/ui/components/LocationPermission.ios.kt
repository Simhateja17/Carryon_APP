package com.example.carryon.ui.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocationRequester(
    onLocation: (lat: Double, lng: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    // iOS location integration — placeholder until CLLocationManager is wired up
    return { onDenied() }
}
