package com.domelabs.scanapp.feature.scan.impl.data.repository

import com.domelabs.scanapp.core.persistence.database.entity.ScanHistoryEntity
import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind
import com.domelabs.scanapp.feature.scan.impl.data.source.ScanHistoryLocalSource
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class ScanHistoryRepositoryImpl(
    private val localSource: ScanHistoryLocalSource,
) : ScanHistoryRepository {

    override fun observeHistory(): Flow<List<ScanHistoryItem>> =
        localSource.observeHistory().map { entities ->
            entities.map { entity ->
                ScanHistoryItem(
                    id = entity.id,
                    timestampEpochMillis = entity.timestampEpochMillis,
                    rawValue = entity.rawValue,
                    codeKind = CodeKind.valueOf(entity.codeKind),
                    codeFormat = CodeFormat.valueOf(entity.codeFormat),
                    source = entity.source.toScanHistorySource(),
                )
            }
        }

    override fun historyItemFlow(id: Long): Flow<ScanHistoryItem> =
        localSource.observeHistory().map { history ->
            history.firstOrNull { it.id == id }?.let { entity ->
                ScanHistoryItem(
                    id = entity.id,
                    timestampEpochMillis = entity.timestampEpochMillis,
                    rawValue = entity.rawValue,
                    codeKind = CodeKind.valueOf(entity.codeKind),
                    codeFormat = CodeFormat.valueOf(entity.codeFormat),
                    source = entity.source.toScanHistorySource(),
                )
            }
        }.filterNotNull()

    override suspend fun registerScan(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScanHistorySource,
    ): ScanHistoryItem? {
        val now = Clock.System.now().toEpochMilliseconds()
        val latest = localSource.getLatestByRawValue(rawValue)
        if (latest != null && (now - latest.timestampEpochMillis) < 10_000L) return null
        val entity = ScanHistoryEntity(
            timestampEpochMillis = now,
            rawValue = rawValue,
            codeKind = codeKind,
            codeFormat = codeFormat,
            source = source.name,
        )
        localSource.insert(entity)
        return ScanHistoryItem(
            id = entity.id,
            timestampEpochMillis = entity.timestampEpochMillis,
            rawValue = entity.rawValue,
            codeKind = CodeKind.valueOf(entity.codeKind),
            codeFormat = CodeFormat.valueOf(entity.codeFormat),
            source = ScanHistorySource.valueOf(entity.source)
        )
    }

    override suspend fun deleteHistoryItem(id: Long) {
        localSource.deleteById(id)
    }

    override suspend fun clearHistory() {
        localSource.clear()
    }
}

private fun String.toScanHistorySource(): ScanHistorySource =
    ScanHistorySource.entries.firstOrNull { it.name == this } ?: ScanHistorySource.CAMERA
