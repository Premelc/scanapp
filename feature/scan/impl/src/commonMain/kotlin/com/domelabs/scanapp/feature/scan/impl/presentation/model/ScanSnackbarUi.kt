package com.domelabs.scanapp.feature.scan.impl.presentation.model

data class ScanSnackbarUi(
    val eventId: Long,
    val rawValue: String,
    val codeKind: String,
    val codeFormat: String,
    val source: String,
    val scannedAtEpochMillis: Long,
)
