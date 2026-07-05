package com.company.carryon.ui.components

import androidx.compose.runtime.Composable

expect class ImagePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberImagePickerLauncher(
    onImagePickFailed: (String) -> Unit = {},
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher
