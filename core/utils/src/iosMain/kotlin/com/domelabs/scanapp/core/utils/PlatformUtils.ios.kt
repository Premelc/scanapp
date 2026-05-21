package com.domelabs.scanapp.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@Composable
actual fun platformActivity(): PlatformActivity {
    return PlatformActivity()
}

actual class PlatformActivity
@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry() = ClipEntry.withPlainText(this)
