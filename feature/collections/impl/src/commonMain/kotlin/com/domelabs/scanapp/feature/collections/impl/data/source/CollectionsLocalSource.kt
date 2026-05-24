package com.domelabs.scanapp.feature.collections.impl.data.source

import com.domelabs.scanapp.core.persistence.database.dao.CollectionWithStats
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

interface CollectionsLocalSource {
    fun observeAllWithStats(): Flow<List<CollectionWithStats>>
    suspend fun getById(id: Long): CollectionEntity?
    fun observeById(id: Long): Flow<CollectionEntity?>
    suspend fun findByName(name: String): CollectionEntity?
    suspend fun insert(entity: CollectionEntity): Long
    suspend fun update(entity: CollectionEntity)
    suspend fun touchUpdatedAt(id: Long, timestamp: Long)
    suspend fun deleteById(id: Long)
}
