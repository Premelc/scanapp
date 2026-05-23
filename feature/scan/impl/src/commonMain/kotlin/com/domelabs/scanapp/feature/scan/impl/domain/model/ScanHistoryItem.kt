package com.domelabs.scanapp.feature.scan.impl.domain.model

import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind

data class ScanHistoryItem(
    val id: Long,
    val timestampEpochMillis: Long,
    val rawValue: String,
    val codeKind: CodeKind,
    val codeFormat: CodeFormat,
    val source: ScanHistorySource,
)

enum class ScanHistorySource {
    CAMERA,
    GALLERY,
}
