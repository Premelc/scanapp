package com.domelabs.scanapp.core.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
actual fun CodeScanner(
    onDetected: (ScannedCode) -> Unit,
    onError: (ScanError) -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    flashEnabled: Boolean,
    cooldownMillis: Long,
    analysisIntervalMillis: Long,
    zoomState: CameraZoomState?,
) {
    // iOS runtime behavior is implemented in v1 as camera-preview placeholder
    // so Android development can proceed while keeping iOS compilation healthy.
    // Detection callbacks are no-op on iOS until AVFoundation scanner wiring lands.
    Box(
        modifier = modifier.background(Color.Black),
    )
}
