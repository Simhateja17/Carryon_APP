package com.company.carryon.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

private const val MaxUploadImageBytes = 900 * 1024

actual class ImagePickerLauncher(
    private val onImagePicked: (ByteArray) -> Unit,
    private val onImagePickFailed: (String) -> Unit
) {
    private var currentDelegate: NSObject? = null

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun launch() {
        val librarySource = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        if (!UIImagePickerController.isSourceTypeAvailable(librarySource)) {
            onImagePickFailed("Photo library is unavailable on this device.")
            return
        }

        val picker = UIImagePickerController().apply {
            sourceType = librarySource
            allowsEditing = true
        }
        val launcher = this
        val delegate = object : NSObject(),
            UIImagePickerControllerDelegateProtocol,
            UINavigationControllerDelegateProtocol {

            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                @Suppress("UNCHECKED_CAST")
                val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage)
                    ?: (didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage)
                val data: NSData? = image?.jpegDataUnderLimit()
                val bytes = data?.bytes?.readBytes(data.length.toInt()) ?: ByteArray(0)
                if (bytes.isEmpty()) {
                    onImagePickFailed("Could not prepare the selected image. Please try again.")
                } else {
                    onImagePicked(bytes)
                }
                picker.dismissViewControllerAnimated(true, completion = null)
                launcher.currentDelegate = null
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, completion = null)
                launcher.currentDelegate = null
            }
        }

        currentDelegate = delegate
        picker.delegate = delegate

        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (rootViewController == null) {
            onImagePickFailed("Could not open the photo library. Please try again.")
            currentDelegate = null
            return
        }
        rootViewController.presentViewController(picker, animated = true, completion = null)
    }
}

@Composable
actual fun rememberImagePickerLauncher(
    onImagePickFailed: (String) -> Unit,
    onImagePicked: (ByteArray) -> Unit
): ImagePickerLauncher {
    val latestOnImagePicked = rememberUpdatedState(onImagePicked)
    val latestOnImagePickFailed = rememberUpdatedState(onImagePickFailed)
    return remember {
        ImagePickerLauncher(
            onImagePicked = { bytes -> latestOnImagePicked.value(bytes) },
            onImagePickFailed = { message -> latestOnImagePickFailed.value(message) }
        )
    }
}

private fun UIImage.jpegDataUnderLimit(): NSData? {
    var quality = 0.8
    var data = UIImageJPEGRepresentation(this, quality)
    while (data != null && data.length > MaxUploadImageBytes.toULong() && quality > 0.35) {
        quality -= 0.1
        data = UIImageJPEGRepresentation(this, quality)
    }
    return data
}
