package com.domelabs.scanapp.feature.scan.impl.data.repository

import com.domelabs.scanapp.core.persistence.database.entity.ScanHistoryEntity
import com.domelabs.scanapp.feature.scan.impl.data.source.ScanHistoryLocalSource
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                    codeKind = entity.codeKind,
                    codeFormat = entity.codeFormat,
                    source = entity.source.toScanHistorySource(),
                )
            }
        }

    override suspend fun registerScan(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScanHistorySource,
    ) {
        val now = System.currentTimeMillis()
        val latest = localSource.getLatestByRawValue(rawValue)
        if (latest != null && (now - latest.timestampEpochMillis) < 10_000L) return

        localSource.insert(
            ScanHistoryEntity(
                timestampEpochMillis = now,
                rawValue = rawValue,
                codeKind = codeKind,
                codeFormat = codeFormat,
                source = source.name,
            )
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
