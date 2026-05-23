package com.domelabs.scanapp.core.capturable

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun CapturableContent(
    state: CapturableState,
    content: @Composable () -> Unit,
    modifier: Modifier,
) {
    val captureController = rememberCaptureController()

    LaunchedEffect(state, captureController) {
        state.captureRequests.collect { onResult ->
            val result = try {
                val imageBitmap = captureController.captureAsync().await()
                val pngBytes = withContext(Dispatchers.Default) {
                    imageBitmap.asAndroidBitmap().toPngBytes()
                }
                CapturableResult.Success(pngBytes)
            } catch (throwable: Throwable) {
                CapturableResult.Error(throwable)
            }
            onResult(result)
        }
    }

    Box(modifier = modifier.capturable(captureController)) {
        content()
    }
}

private fun Bitmap.toPngBytes(): ByteArray {
    // Flatten alpha onto white so target share apps don't render transparent pixels as black.
    val flattened = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    Canvas(flattened).apply {
        drawColor(0xFFFFFFFF.toInt())
        drawBitmap(this@toPngBytes, 0f, 0f, null)
    }
    val stream = ByteArrayOutputStream()
    flattened.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
