package com.domelabs.scanapp.feature.scan.impl.domain.repository

import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun observeHistory(): Flow<List<ScanHistoryItem>>
    fun historyItemFlow(id:Long): Flow<ScanHistoryItem>
    suspend fun registerScan(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScanHistorySource,
    ): ScanHistoryItem?

    suspend fun deleteHistoryItem(id: Long)

    suspend fun clearHistory()
}
