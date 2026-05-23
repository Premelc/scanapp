package com.domelabs.scanapp.core.capturable

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.FileOutputStream

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CapturableContent(
    content: @Composable () -> Unit,
    captureButton: @Composable (() -> Unit) -> Unit,
    modifier: Modifier,
) {
    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.capturable(captureController)) {
            content()
        }
        captureButton {
            scope.launch {
                val bitmapAsync = captureController.captureAsync()
                try {
                    val bitmap = bitmapAsync.await()
                    shareBitmap(
                        context = context,
                        bitmap = bitmap.asAndroidBitmap(),
                    )
                } catch (error: Throwable) {
                    // Error occurred, do something.
                }
            }
        }
    }
}

fun shareBitmap(context: Context, bitmap: Bitmap) {
    val cacheDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val file = File(cacheDir, "shared_image.png")

    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    file.deleteOnExit()

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
}