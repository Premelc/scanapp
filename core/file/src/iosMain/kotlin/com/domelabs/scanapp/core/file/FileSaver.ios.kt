package com.domelabs.scanapp.utils

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.darwin.NSObject
import platform.Foundation.*
import kotlinx.cinterop.convert
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFileSaver(): (content: String, defaultFilename: String, onResult: (Boolean) -> Unit) -> Unit {
    return { content, defaultFilename, onResult ->

        val viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (viewController == null) {
            onResult(false)
        } else {
            val tmpDir = NSTemporaryDirectory()
            val filePath = "$tmpDir/$defaultFilename"
            val fileURL = NSURL.fileURLWithPath(filePath)

            val bytes = content.encodeToByteArray()
            val success = bytes.usePinned { pinned ->
                NSData.dataWithBytes(pinned.addressOf(0), bytes.size.convert())
                    .writeToURL(fileURL, true)
            }

            if (!success) {
                onResult(false)
            } else {
                val picker = UIDocumentPickerViewController(
                    uRL = fileURL,
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeExportToService
                )

                picker.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                        onResult(false)
                    }

                    override fun documentPicker(
                        controller: UIDocumentPickerViewController,
                        didPickDocumentsAtURLs: List<*>,
                    ) {
                        onResult(true)
                    }
                }

                viewController.presentViewController(picker, animated = true, completion = null)
            }
        }
    }
}
