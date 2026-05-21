package com.domelabs.scanapp.core.media

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberFileSaver(): (content: String, defaultFilename: String, onResult: (Boolean) -> Unit) -> Unit {
    val context = LocalContext.current
    var pendingContent: String? = null
    var pendingCallback: ((Boolean) -> Unit)? = null

    val createDocumentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val content = pendingContent
                val callback = pendingCallback
                if (content != null && callback != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                outputStream.write(content.toByteArray())
                            }
                            withContext(Dispatchers.Main) {
                                callback(true)
                            }
                        } catch (_: Exception) {
                            withContext(Dispatchers.Main) {
                                callback(false)
                            }
                        }
                    }
                }
            } ?: pendingCallback?.invoke(false)
        } else {
            pendingCallback?.invoke(false)
        }
        pendingContent = null
        pendingCallback = null
    }

    return { content, defaultFilename, onResult ->
        pendingContent = content
        pendingCallback = onResult

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, defaultFilename)
        }
        createDocumentLauncher.launch(intent)
    }
}
