package com.domelabs.cugaapp.required

import com.domelabs.cugaapp.required.image.MediaItem
import io.github.vinceglb.filekit.utils.toByteArray
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

class IOSFile(private val path: String, override val contentType: String) : MultiplatformFile {
    override val name: String
        get() = path.substringAfterLast("/")

    override suspend fun readBytes(): ByteArray {
        val nsData = NSData.dataWithContentsOfFile(path) ?: return ByteArray(0)
        return nsData.toByteArray()
    }

    override suspend fun getMime(): String? {
        TODO("Not yet implemented")
    }
}

actual fun MediaItem.toFile(): MultiplatformFile = IOSFile(
    path = this.uri,
    contentType = when (this) {
        is MediaItem.Image -> "image/jpeg"
        is MediaItem.Video -> "video/mp4"
    }
)