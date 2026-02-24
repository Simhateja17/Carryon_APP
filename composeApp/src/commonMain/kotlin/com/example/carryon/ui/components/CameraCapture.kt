package com.example.carryon.ui.components

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that, when invoked, will:
 *  1. Request CAMERA permission if not yet granted
 *  2. Launch the device camera to take a photo
 *  3. Call [onImageCaptured] with the raw JPEG bytes on success
 *  4. Call [onDenied] if the permission is refused or camera is cancelled
 */
@Composable
expect fun rememberCameraCapture(
    onImageCaptured: (ByteArray) -> Unit,
    onDenied: () -> Unit = {}
): () -> Unit
