package com.domelabs.scanapp.core.media

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberFilePickerWithInfo(
    onPicked: (PickedFileInfo) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                copyToCacheWithInfo(context, it)?.let(onPicked)
            }
        }

    return {
        launcher.launch(arrayOf("*/*"))
    }
}

private fun copyToCacheWithInfo(context: Context, uri: Uri): PickedFileInfo? {
    val fileName = getDisplayName(context, uri) ?: "file_${System.currentTimeMillis()}"
    val fileSize = getFileSize(context, uri)
    val cacheFile = File(context.cacheDir, fileName)
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            cacheFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        PickedFileInfo(
            path = cacheFile.absolutePath,
            name = fileName,
            size = fileSize ?: cacheFile.length()
        )
    } catch (_: Exception) {
        null
    }
}

private fun getDisplayName(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    ) ?: return null
    return cursor.use {
        if (!it.moveToFirst()) return null
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex == -1) null else it.getString(nameIndex)
    }
}

private fun getFileSize(context: Context, uri: Uri): Long? {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.SIZE),
        null,
        null,
        null
    ) ?: return null
    return cursor.use {
        if (!it.moveToFirst()) return null
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex == -1) null else it.getLong(sizeIndex)
    }
}
