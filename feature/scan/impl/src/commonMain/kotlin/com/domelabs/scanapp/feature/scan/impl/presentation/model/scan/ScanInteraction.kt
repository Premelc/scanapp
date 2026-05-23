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
    data class GalleryCodeDetected(val code: ScannedCode) : ScanInteraction
    data object GalleryCodeNotFound : ScanInteraction
    data object RetryAfterError : ScanInteraction
    data class CodeDetected(val code: ScannedCode) : ScanInteraction
    data class ScanFailed(val error: ScanError) : ScanInteraction
}
