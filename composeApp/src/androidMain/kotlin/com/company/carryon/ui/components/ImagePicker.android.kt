package com.company.carryon.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

private const val MaxUploadImageBytes = 900 * 1024
private const val MaxUploadImageDimensionPx = 1280

actual class ImagePickerLauncher(private val onLaunch: () -> Unit) {
    actual fun launch() = onLaunch()
}

@Composable
actual fun rememberImagePickerLauncher(
    onImagePickFailed: (String) -> Unit,
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher {
    val context = LocalContext.current
    val latestOnImagePicked = rememberUpdatedState(onImagePicked)
    val latestOnImagePickFailed = rememberUpdatedState(onImagePickFailed)
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            val bytes = context.readUploadImageBytes(uri)
            if (bytes.isEmpty()) {
                latestOnImagePickFailed.value("Could not read the selected image. Please try again.")
            } else {
                latestOnImagePicked.value(bytes)
            }
        } catch (_: Exception) {
            latestOnImagePickFailed.value("Could not read the selected image. Please try again.")
        }
    }

    return remember(pickerLauncher) {
        ImagePickerLauncher { pickerLauncher.launch("image/*") }
    }
}

private fun Context.readUploadImageBytes(uri: Uri): ByteArray =
    contentResolver.openInputStream(uri)?.use { it.readBytes().compressedForUpload() } ?: ByteArray(0)

private fun ByteArray.compressedForUpload(): ByteArray {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(this, 0, size, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return this

    val largestBound = max(bounds.outWidth, bounds.outHeight)
    var sampleSize = 1
    while (largestBound / sampleSize > MaxUploadImageDimensionPx) {
        sampleSize *= 2
    }

    val bitmap = BitmapFactory.decodeByteArray(
        this,
        0,
        size,
        BitmapFactory.Options().apply { inSampleSize = sampleSize }
    ) ?: return this

    return try {
        compressBitmapUnderLimit(bitmap)
    } finally {
        bitmap.recycle()
    }
}

private fun compressBitmapUnderLimit(source: Bitmap): ByteArray {
    var working = scaleBitmapToMaxDimension(source, MaxUploadImageDimensionPx)
    var shouldRecycleWorking = working !== source

    try {
        var quality = 85
        while (quality >= 45) {
            val bytes = working.toJpegBytes(quality)
            if (bytes.size <= MaxUploadImageBytes) return bytes
            quality -= 10
        }

        repeat(6) {
            val nextWidth = (working.width * 0.85f).roundToInt().coerceAtLeast(640)
            val nextHeight = (working.height * 0.85f).roundToInt().coerceAtLeast(640)
            if (nextWidth == working.width && nextHeight == working.height) {
                return working.toJpegBytes(45)
            }

            val scaled = Bitmap.createScaledBitmap(working, nextWidth, nextHeight, true)
            if (shouldRecycleWorking) working.recycle()
            working = scaled
            shouldRecycleWorking = true

            quality = 75
            while (quality >= 45) {
                val bytes = working.toJpegBytes(quality)
                if (bytes.size <= MaxUploadImageBytes) return bytes
                quality -= 10
            }
        }

        return working.toJpegBytes(45)
    } finally {
        if (shouldRecycleWorking) working.recycle()
    }
}

private fun scaleBitmapToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val largestSide = max(bitmap.width, bitmap.height)
    if (largestSide <= maxDimension) return bitmap

    val scale = maxDimension.toFloat() / largestSide.toFloat()
    val targetWidth = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
    val targetHeight = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}

private fun Bitmap.toJpegBytes(quality: Int): ByteArray =
    ByteArrayOutputStream().use { output ->
        compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), output)
        output.toByteArray()
    }
