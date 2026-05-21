package com.domelabs.scanapp.core.media

import android.content.Context
import androidx.core.net.toUri
import org.koin.mp.KoinPlatformTools
import java.io.File

actual fun MediaItem.toFile(): MultiplatformFile = AndroidFileFromPath(
    path = this.uri,
    contentType = when (this) {
        is MediaItem.Image -> "image/jpeg"
        is MediaItem.Video -> "video/mp4"
    }
)

class AndroidFileFromPath(
    private val path: String,
    private val fileName: String = path.substringAfterLast("/"),
    override val contentType: String,
) : MultiplatformFile {
    val context = KoinPlatformTools.defaultContext().get().get<Context>()

    override val name: String
        get() = fileName

    override suspend fun getMime() = context.contentResolver.getType(path.toUri()) ?: contentType

    override suspend fun readBytes(): ByteArray {
        return if (path.startsWith("content://")) {
            context.contentResolver.openInputStream(path.toUri())!!.use { it.readBytes() }
        } else {
            File(path).readBytes()
        }
    }
}