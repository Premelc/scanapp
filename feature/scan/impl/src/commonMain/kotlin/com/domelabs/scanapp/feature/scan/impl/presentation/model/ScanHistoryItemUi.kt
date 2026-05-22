package com.domelabs.scanapp.feature.scan.impl.presentation.model

import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import kotlin.math.max

data class ScanHistoryItemUi(
    val id: Long,
    val rawValue: String,
    val codeKind: String,
    val codeFormat: String,
    val source: String,
    val scannedAtEpochMillis: Long,
    val subtitle: String,
    val relativeTime: String,
)

fun ScanHistoryItem.toUi(nowEpochMillis: Long): ScanHistoryItemUi {
    val relative = (nowEpochMillis - timestampEpochMillis).toRelativeTimeText()
    return ScanHistoryItemUi(
        id = id,
        rawValue = rawValue,
        codeKind = codeKind,
        codeFormat = codeFormat,
        source = source.name,
        scannedAtEpochMillis = timestampEpochMillis,
        subtitle = "$codeKind • $codeFormat • ${source.name}",
        relativeTime = relative,
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
