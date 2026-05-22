package com.domelabs.scanapp.feature.scan.impl.presentation.model

import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode

sealed interface ScanInteraction {
    data object OpenHistoryDrawer : ScanInteraction
    data object CloseHistoryDrawer : ScanInteraction
    data class DeleteHistoryItem(val id: Long) : ScanInteraction
    data object ClearHistory : ScanInteraction
    data object ToggleFlashlight : ScanInteraction
    data object RequestCameraPermission : ScanInteraction
    data object OpenGalleryPicker : ScanInteraction
    data object RetryAfterError : ScanInteraction
    data class CodeDetected(val code: ScannedCode) : ScanInteraction
    data class ScanFailed(val error: ScanError) : ScanInteraction
}
