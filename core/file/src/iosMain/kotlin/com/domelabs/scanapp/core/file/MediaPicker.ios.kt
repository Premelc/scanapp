package com.domelabs.scanapp.presentation.bugReport.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController
import platform.Foundation.NSURL
import platform.UniformTypeIdentifiers.UTTypeImage
import platform.UniformTypeIdentifiers.UTTypeMovie
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.PhotosUI.PHPickerResult
import platform.darwin.NSObject
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun rememberMediaPicker(
    onMediaPicked: (List<MediaItem>) -> Unit
): () -> Unit {
    // iOS implementation using PHPickerViewController
    val pickerLauncher = {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        
        val configuration = PHPickerConfiguration().apply {
            setSelectionLimit(5)
            setFilter(platform.PhotosUI.PHPickerFilter.anyFilterMatchingSubtypes(
                listOf(UTTypeImage.identifier, UTTypeMovie.identifier)
            ))
        }
        
        val picker = PHPickerViewController(configuration = configuration)
        
        val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                
                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                // Handle results - would need more complex async handling for iOS
                // For now, return empty list (iOS implementation needs more work)
                onMediaPicked(emptyList())
            }
        }
        
        picker.setDelegate(delegate)
        rootViewController?.presentViewController(picker, true, null)
    }
    
    return pickerLauncher
}