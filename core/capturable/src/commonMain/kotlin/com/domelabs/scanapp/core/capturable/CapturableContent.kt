package com.domelabs.scanapp.core.capturable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CapturableContent(
    content: @Composable () -> Unit,
    captureButton: @Composable (() -> Unit) -> Unit,
    modifier: Modifier = Modifier,
)
