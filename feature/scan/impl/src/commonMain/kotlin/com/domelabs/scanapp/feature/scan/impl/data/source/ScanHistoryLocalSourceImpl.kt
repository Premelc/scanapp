package com.domelabs.scanapp.feature.scan.impl.data.source

import com.domelabs.scanapp.core.persistence.database.dao.ScanHistoryDao
import com.domelabs.scanapp.core.persistence.database.entity.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanHistoryLocalSourceImpl(
    private val dao: ScanHistoryDao,
) : ScanHistoryLocalSource {
    override fun observeHistory(): Flow<List<ScanHistoryEntity>> = dao.observeLatest()

    override suspend fun getLatestByRawValue(rawValue: String): ScanHistoryEntity? =
        dao.getLatestByRawValue(rawValue)

    override suspend fun insert(item: ScanHistoryEntity): Long {
        val id = dao.insert(item)
        dao.trimToLimit(50)
        return id
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
