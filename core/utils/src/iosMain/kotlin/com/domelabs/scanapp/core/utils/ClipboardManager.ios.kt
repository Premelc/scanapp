package com.domelabs.scanapp.core.utils

import androidx.compose.runtime.Composable
import platform.UIKit.UIPasteboard

@Composable
actual fun rememberClipboardManager(): (String) -> Unit {
    return { text: String ->
        UIPasteboard.generalPasteboard.string = text
    }
}
