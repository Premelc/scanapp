package com.domelabs.scanapp.core.media

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
actual fun rememberMediaPicker(
    onMediaPicked: (List<MediaItem>) -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        val mediaItems = uris.mapNotNull { uri ->
            uri.toMediaItem(context)
        }
        if (mediaItems.isNotEmpty()) {
            onMediaPicked(mediaItems)
        }
    }

    return {
        scope.launch {
            launcher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                )
            )
        }
    }
}

private fun Uri.toMediaItem(context: Context): MediaItem? {
    return try {
        val mimeType = context.contentResolver.getType(this) ?: "*/*"
        var fileName = "unknown"
        var fileSize = 0L

        context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex) ?: "unknown"
                }
                if (sizeIndex >= 0) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }

        val isImage = mimeType.startsWith("image/")
        val isVideo = mimeType.startsWith("video/")

        if (isImage) {
            MediaItem.Image(
                uri = this.toString(),
                name = fileName,
                size = fileSize,
                aspectRatio = 1f
            )
        } else if (isVideo) {
            MediaItem.Video(
                uri = this.toString(),
                name = fileName,
                size = fileSize,
                aspectRatio = 16f / 9f,
                thumbnailUrl = null
            )
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}