package com.domelabs.scanapp.core.capturable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
actual fun CapturableContent(
    state: CapturableState,
    content: @Composable () -> Unit,
    modifier: Modifier,
) {
    LaunchedEffect(state) {
        state.captureRequests.collect { onResult ->
            onResult(
                CapturableResult.Error(
                    NotImplementedError("iOS capture is not implemented yet."),
                )
            )
        }
    }

    Box(modifier = modifier) {
        content()
    }
}