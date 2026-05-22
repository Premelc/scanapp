package com.domelabs.scanapp.feature.scan.impl.data.source

import com.domelabs.scanapp.core.persistence.database.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

interface ScanHistoryLocalSource {
    fun observeHistory(): Flow<List<ScanHistoryEntity>>

    suspend fun getLatestByRawValue(rawValue: String): ScanHistoryEntity?

    suspend fun insert(item: ScanHistoryEntity)

    suspend fun deleteById(id: Long)

    suspend fun clear()
}
