package com.domelabs.scanapp.feature.scan.impl.presentation.model.scan

import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode

sealed interface ScanInteraction {
    data object OpenMenuDrawer : ScanInteraction
    data object CloseMenuDrawer : ScanInteraction
    data object NavigateToHistory : ScanInteraction
    data object NavigateToSettings : ScanInteraction
    data object NavigateToCollections : ScanInteraction
    data object ToggleFlashlight : ScanInteraction
    data object RequestCameraPermission : ScanInteraction
    data object OpenGalleryPicker : ScanInteraction
    data object RetryAfterError : ScanInteraction
    data class OpenScanDetails(
        val rawValue: String,
        val codeKind: String,
        val codeFormat: String,
        val source: String,
        val scannedAtEpochMillis: Long,
    ) : ScanInteraction
    data class CodeDetected(val code: ScannedCode) : ScanInteraction
    data class ScanFailed(val error: ScanError) : ScanInteraction
}
