package com.domelabs.scanapp.feature.scan.impl

import com.domelabs.scanapp.core.scan.ScanError
import com.domelabs.scanapp.core.scan.ScannedCode

enum class ScanPermissionState {
    Unknown,
    Granted,
    ShowRationale,
    Denied,
}

data class ScanViewState(
    val permission: ScanPermissionState = ScanPermissionState.Unknown,
    val flashEnabled: Boolean = false,
    val lastDetection: ScannedCode? = null,
    val error: ScanError? = null,
    val isScannerActive: Boolean = true,
)
