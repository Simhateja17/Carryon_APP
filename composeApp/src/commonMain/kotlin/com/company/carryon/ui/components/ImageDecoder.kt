package com.company.carryon.ui.components

import androidx.compose.ui.graphics.ImageBitmap

expect fun decodeImageBytes(bytes: ByteArray): ImageBitmap?
