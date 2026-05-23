package com.domelabs.scanapp.core.capturable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableSharedFlow

sealed interface CapturableResult {
    data class Success(val pngBytes: ByteArray) : CapturableResult
    data class Error(val throwable: Throwable) : CapturableResult
}

@Stable
class CapturableState internal constructor() {
    internal val captureRequests = MutableSharedFlow<(CapturableResult) -> Unit>(
        extraBufferCapacity = 16,
    )

    fun capture(onResult: (CapturableResult) -> Unit = {}) {
        val emitted = captureRequests.tryEmit(onResult)
        if (!emitted) {
            onResult(
                CapturableResult.Error(
                    IllegalStateException("Capture queue is full."),
                )
            )
        }
    }
}

@Composable
fun rememberCapturableState(): CapturableState {
    return remember { CapturableState() }
}

@Composable
expect fun CapturableContent(
    state: CapturableState,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
)
