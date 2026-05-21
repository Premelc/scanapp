package com.domelabs.scanapp.core.media

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberSshKeyContentPicker(onPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                readContent(context, it)?.let(onPicked)
            }
        }

    return {
        launcher.launch(arrayOf("*/*"))
    }
}

private fun readContent(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader().use { reader ->
                reader.readText()
            }
        }
    } catch (_: Exception) {
        null
    }
}
