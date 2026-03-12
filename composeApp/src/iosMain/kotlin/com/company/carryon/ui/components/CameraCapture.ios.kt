package com.company.carryon.ui.components

import androidx.compose.runtime.Composable

@Composable
actual fun rememberCameraCapture(
    onImageCaptured: (ByteArray) -> Unit,
    onDenied: () -> Unit
): () -> Unit {
    // iOS camera integration — placeholder until UIImagePickerController is wired up
    return { onDenied() }
}
