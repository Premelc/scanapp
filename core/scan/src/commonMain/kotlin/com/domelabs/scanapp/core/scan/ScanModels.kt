package com.domelabs.scanapp.core.scan

enum class CodeKind {
    QR,
    BARCODE,
}

enum class CodeFormat {
    QR_CODE,
    AZTEC,
    DATA_MATRIX,
    PDF_417,
    CODE_39,
    CODE_93,
    CODE_128,
    EAN_8,
    EAN_13,
    UPC_A,
    UPC_E,
    ITF,
    CODABAR,
    UNKNOWN,
}

data class ScannedCode(
    val kind: CodeKind,
    val format: CodeFormat,
    val rawValue: String,
)

sealed interface ScanError {
    data object CameraUnavailable : ScanError
    data class AnalysisFailed(val message: String?) : ScanError
}
