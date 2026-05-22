package com.domelabs.scanapp.feature.scan.impl.presentation.model.details
data class ScanDetailsViewState(
    val matrixState: CodePreviewState = CodePreviewState.Loading,
    val scannedAtLabel: String = "",
)