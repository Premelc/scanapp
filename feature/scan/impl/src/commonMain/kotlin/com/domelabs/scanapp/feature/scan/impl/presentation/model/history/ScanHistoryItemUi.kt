package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import kotlin.math.max

data class ScanHistoryItemUi(
    val id: Long,
    val displayName: String,
    val rawValue: String,
    val codeKind: CodeKind,
    val codeFormat: CodeFormat,
    val source: String,
    val scannedAtEpochMillis: Long,
    val subtitle: String,
    val relativeTime: String,
    val collectionName: String,
    val collectionColorHex: String,
)

fun ScannedItem.toUi(
    nowEpochMillis: Long,
    collectionsById: Map<Long, CollectionSummary>,
): ScanHistoryItemUi {
    val relative = (nowEpochMillis - timestampEpochMillis).toRelativeTimeText()
    val collection = collectionsById[collectionId]?.collection
    val collectionName = collection?.name ?: "Unspecified"
    val collectionColor = collection?.colorHex ?: "#D9D9D9"
    return ScanHistoryItemUi(
        id = id,
        displayName = customName ?: rawValue,
        rawValue = rawValue,
        codeKind = codeKind,
        codeFormat = codeFormat,
        source = source.name,
        scannedAtEpochMillis = timestampEpochMillis,
        subtitle = "$codeKind • $codeFormat • ${source.name}",
        relativeTime = relative,
        collectionName = collectionName,
        collectionColorHex = collectionColor,
    )
}

private fun Long.toRelativeTimeText(): String {
    val delta = max(this, 0L)
    val seconds = delta / 1_000L
    return when {
        seconds < 5 -> "just now"
        seconds < 60 -> "${seconds}s ago"
        seconds < 3_600 -> "${seconds / 60}m ago"
        seconds < 86_400 -> "${seconds / 3_600}h ago"
        else -> "${seconds / 86_400}d ago"
    }
}
