package com.domelabs.scanapp.utils

import androidx.compose.runtime.Composable

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.darwin.NSObject
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL

@OptIn(BetaInteropApi::class)
@Composable
actual fun rememberJsonFileContentPicker(onPicked: (String) -> Unit): () -> Unit {
    return {
        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (viewController != null) {
            val picker = UIDocumentPickerViewController(
                documentTypes = listOf("public.json"),
                inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen
            )

            picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(
                    controller: UIDocumentPickerViewController,
                    didPickDocumentsAtURLs: List<*>,
                ) {
                    val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
                    val data = NSData.dataWithContentsOfURL(url)
                    val content =
                        data?.let { NSString.create(it, NSUTF8StringEncoding) as String } ?: ""
                    onPicked(content)
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    // Do nothing or handle cancel if needed
                }
            }

            viewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}
