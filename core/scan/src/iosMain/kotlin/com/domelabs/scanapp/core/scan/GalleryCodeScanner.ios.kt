package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGalleryCodeScanner(): GalleryCodeScanner {
    return object : GalleryCodeScanner {
        override suspend fun scanCodeFromImageUri(uri: String): ScannedCode? = null
    }
}
