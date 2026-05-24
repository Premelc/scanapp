package com.domelabs.scanapp.feature.collections.impl.data.source

import com.domelabs.scanapp.core.persistence.database.dao.ScannedItemDao
import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import kotlinx.coroutines.flow.Flow

class ScannedItemsLocalSourceImpl(
    private val dao: ScannedItemDao,
) : ScannedItemsLocalSource {
    override fun observeRecent(limit: Int): Flow<List<ScannedItemEntity>> = dao.observeRecent(limit)
    override fun observeByCollection(collectionId: Long): Flow<List<ScannedItemEntity>> =
        dao.observeByCollection(collectionId)
    override fun searchInCollection(collectionId: Long, query: String): Flow<List<ScannedItemEntity>> =
        dao.searchInCollection(collectionId, query)
    override suspend fun getById(id: Long): ScannedItemEntity? = dao.getById(id)
    override fun observeById(id: Long): Flow<ScannedItemEntity?> = dao.observeById(id)
    override suspend fun getLatestByRawValue(rawValue: String): ScannedItemEntity? =
        dao.getLatestByRawValue(rawValue)
    override suspend fun insert(entity: ScannedItemEntity): Long = dao.insert(entity)
    override suspend fun setCustomName(id: Long, customName: String?) = dao.setCustomName(id, customName)
    override suspend fun setCollection(id: Long, targetCollectionId: Long) =
        dao.setCollection(id, targetCollectionId)
    override suspend fun reassignAllInCollection(sourceCollectionId: Long, targetCollectionId: Long) =
        dao.reassignAllInCollection(sourceCollectionId, targetCollectionId)
    override suspend fun deleteAllInCollection(collectionId: Long) =
        dao.deleteAllInCollection(collectionId)
    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}
