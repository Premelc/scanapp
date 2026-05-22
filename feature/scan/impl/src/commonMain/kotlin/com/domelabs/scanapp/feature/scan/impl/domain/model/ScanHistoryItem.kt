package com.domelabs.scanapp.feature.scan.impl.domain.model

data class ScanHistoryItem(
    val id: Long,
    val timestampEpochMillis: Long,
    val rawValue: String,
    val codeKind: String,
    val codeFormat: String,
    val source: ScanHistorySource,
)

enum class ScanHistorySource {
    CAMERA,
    GALLERY,
}
