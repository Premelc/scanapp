package com.domelabs.scanapp.core.media

import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaItem {
    val uri: String
    val name: String
    val size: Long
    val dateAdded: Long
    val aspectRatio: Float

    @Serializable
    data class Image(
        override val uri: String,
        override val name: String,
        override val size: Long,
        override val aspectRatio: Float,
        override val dateAdded: Long = 0L,
    ) : MediaItem

    @Serializable
    data class Video(
        override val uri: String,
        override val name: String,
        override val size: Long,
        override val dateAdded: Long = 0L,
        override val aspectRatio: Float,
        val thumbnailUrl: String? = null,
    ) : MediaItem
}

interface MultiplatformFile {
    val name: String
    val contentType: String
    suspend fun readBytes(): ByteArray
    suspend fun getMime(): String?
}

expect fun MediaItem.toFile(): MultiplatformFile

suspend fun MultiplatformFile.tailBytes(maxBytes: Int = 1_000_000): ByteArray {
    val bytes = readBytes()
    return if (bytes.size <= maxBytes) {
        bytes
    } else {
        bytes.copyOfRange(bytes.size - maxBytes, bytes.size)
    }
}
