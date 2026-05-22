package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable
import kotlinx.cinterop.refTo
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIViewController

private class IosCodeShareActions : CodeShareActions {
    override fun shareText(text: String) {
        val viewController: UIViewController =
            UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val items = NSArray.arrayWithObject(text)
        val activityVC = UIActivityViewController(activityItems = items, applicationActivities = null)
        viewController.presentViewController(activityVC, animated = true, completion = null)
    }

    override fun shareImage(pngBytes: ByteArray, fileName: String) {
        val viewController: UIViewController =
            UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val imageData = NSData.create(bytes = pngBytes.refTo(0), length = pngBytes.size.toULong())
        val image = UIImage(data = imageData) ?: return
        val items = NSArray.arrayWithObject(image)
        val activityVC = UIActivityViewController(activityItems = items, applicationActivities = null)
        viewController.presentViewController(activityVC, animated = true, completion = null)
    }
}

@Composable
actual fun rememberCodeShareActions(): CodeShareActions = IosCodeShareActions()

actual object ScanCodePlatform {
    actual fun generateMatrix(rawValue: String, codeFormat: String): GeneratedCodeMatrix? = null

    actual fun generatePng(rawValue: String, codeFormat: String, sizePx: Int): ByteArray? = null
}
