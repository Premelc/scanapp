package com.domelabs.scanapp.core.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CodeScanner(
    onDetected: (ScannedCode) -> Unit,
    onError: (ScanError) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    flashEnabled: Boolean = false,
    cooldownMillis: Long = 2_000L,
)
