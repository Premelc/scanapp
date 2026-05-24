package com.domelabs.scanapp.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberClipboardManager(): (String) -> Unit {
    val context = LocalContext.current
    return { text ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scan value", text)
        clipboard.setPrimaryClip(clip)
    }
}
