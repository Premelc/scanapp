package com.domelabs.scanapp.utils

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.firestore.invoke
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.darwin.NSObject

@Composable
actual fun rememberFilePickerWithInfo(
    onPicked: (PickedFileInfo) -> Unit
): () -> Unit {
    return {
        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        val picker = UIDocumentPickerViewController(
            documentTypes = listOf("public.data"),
            inMode = UIDocumentPickerMode.UIDocumentPickerModeOpen
        )

        picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
            override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                if (url != null) {
                    val info = PickedFileInfo(
                        name = url.lastPathComponent ?: "",
                        path = url.path ?: "",
                        size = 0L
                    )
                    onPicked(info)
                }
            }
        }

        viewController?.presentViewController(picker, animated = true, completion = null)
    }
}

