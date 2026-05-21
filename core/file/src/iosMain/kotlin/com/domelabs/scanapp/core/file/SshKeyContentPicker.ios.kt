package com.domelabs.scanapp.utils

import androidx.compose.runtime.Composable
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.*
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(BetaInteropApi::class)
@Composable
actual fun rememberSshKeyContentPicker(onPicked: (String) -> Unit): () -> Unit {
    return {
        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (viewController != null) {
            val picker = UIDocumentPickerViewController(
                documentTypes = listOf(
                    "public.ssh-authentication-private-key",
                    "public.ssh-authentication-public-key"
                ),
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
                    // Optionally handle cancel
                }
            }

            viewController.presentViewController(picker, animated = true, completion = null)
        }
    }
}
