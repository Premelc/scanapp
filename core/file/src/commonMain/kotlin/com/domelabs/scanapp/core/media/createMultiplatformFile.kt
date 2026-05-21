package com.domelabs.scanapp.core.media

expect fun createMultiplatformFile(path: String, mimeType: String): MultiplatformFile
expect suspend fun MediaItem.compress(): MediaItem
expect suspend fun compressAndScaleImage(
    imageUri: String,
    maxDimension: Int = 1280,
    compressQuality: Int = 75,
): String?