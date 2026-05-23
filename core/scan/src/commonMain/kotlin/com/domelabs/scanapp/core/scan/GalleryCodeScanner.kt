package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable

interface GalleryCodeScanner {
    suspend fun scanCodeFromImageUri(uri: String): ScannedCode?
}

@Composable
expect fun rememberGalleryCodeScanner(): GalleryCodeScanner
