package com.domelabs.scanapp.core.media

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
actual fun rememberShareUtil(): (text: String) -> Unit {
    val context = LocalContext.current
    return { text ->
        shareText(context, text)
    }
}

private fun shareText(context: Context, text: String) {
    val timestamp = System.currentTimeMillis()
    val fileName = "swissh_export_$timestamp.json"
    val file = File(context.cacheDir, fileName)

    file.writeText(text)

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "SwiSSH Configuration Export")
        putExtra(Intent.EXTRA_TEXT, "Here is my SwiSSH configuration export.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Share SwiSSH Export")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}
