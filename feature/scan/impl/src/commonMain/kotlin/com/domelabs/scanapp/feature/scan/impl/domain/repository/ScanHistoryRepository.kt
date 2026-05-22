package com.domelabs.scanapp.feature.scan.impl.domain.repository

import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistorySource
import kotlinx.coroutines.flow.Flow

interface ScanHistoryRepository {
    fun observeHistory(): Flow<List<ScanHistoryItem>>

    suspend fun registerScan(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScanHistorySource,
    ): Boolean

    suspend fun deleteHistoryItem(id: Long)

    suspend fun clearHistory()
}
