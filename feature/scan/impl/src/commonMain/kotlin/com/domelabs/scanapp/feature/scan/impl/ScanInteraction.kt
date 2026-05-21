package com.domelabs.scanapp.feature.scan.impl

import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode

sealed interface ScanInteraction {
    data object ToggleFlashlight : ScanInteraction
    data object RequestCameraPermission : ScanInteraction
    data object OpenGalleryPicker : ScanInteraction
    data object RetryAfterError : ScanInteraction
    data object Resumed : ScanInteraction
    data object Paused : ScanInteraction
    data class CodeDetected(val code: ScannedCode) : ScanInteraction
    data class ScanFailed(val error: ScanError) : ScanInteraction
}
