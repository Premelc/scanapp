package com.domelabs.scanapp.utils

import androidx.compose.runtime.Composable
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.Foundation.NSArray
import platform.Foundation.arrayWithObject

@Composable
actual fun rememberShareUtil(): (text: String) -> Unit {
    return { text ->
        val viewController: UIViewController? =
            UIApplication.sharedApplication.keyWindow?.rootViewController
        if (viewController != null) {
            val items = NSArray.arrayWithObject(text)
            val activityVC =
                UIActivityViewController(activityItems = items, applicationActivities = null)

            viewController.presentViewController(activityVC, animated = true, completion = null)
        }
    }
}
