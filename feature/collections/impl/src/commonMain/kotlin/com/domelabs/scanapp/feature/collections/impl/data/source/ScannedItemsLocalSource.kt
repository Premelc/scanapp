package com.domelabs.scanapp.feature.collections.impl.data.source

import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import kotlinx.coroutines.flow.Flow

interface ScannedItemsLocalSource {
    fun observeRecent(limit: Int): Flow<List<ScannedItemEntity>>
    fun observeByCollection(collectionId: Long): Flow<List<ScannedItemEntity>>
    fun searchInCollection(collectionId: Long, query: String): Flow<List<ScannedItemEntity>>
    suspend fun getById(id: Long): ScannedItemEntity?
    fun observeById(id: Long): Flow<ScannedItemEntity?>
    suspend fun getLatestByRawValue(rawValue: String): ScannedItemEntity?
    suspend fun insert(entity: ScannedItemEntity): Long
    suspend fun setCustomName(id: Long, customName: String?)
    suspend fun setCollection(id: Long, targetCollectionId: Long)
    suspend fun reassignAllInCollection(sourceCollectionId: Long, targetCollectionId: Long)
    suspend fun deleteAllInCollection(collectionId: Long)
    suspend fun deleteById(id: Long)
}
