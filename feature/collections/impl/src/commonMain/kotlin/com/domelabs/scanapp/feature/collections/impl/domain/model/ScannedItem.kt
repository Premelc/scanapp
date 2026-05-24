package com.domelabs.scanapp.feature.collections.impl.domain.model

import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind

data class ScannedItem(
    val id: Long,
    val collectionId: Long,
    val timestampEpochMillis: Long,
    val rawValue: String,
    val codeKind: CodeKind,
    val codeFormat: CodeFormat,
    val source: ScannedItemSource,
    val customName: String?,
)

enum class ScannedItemSource {
    CAMERA,
    GALLERY,
}
