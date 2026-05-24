package com.domelabs.scanapp.feature.collections.impl.data.source

import com.domelabs.scanapp.core.persistence.database.dao.CollectionDao
import com.domelabs.scanapp.core.persistence.database.dao.CollectionWithStats
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity
import kotlinx.coroutines.flow.Flow

class CollectionsLocalSourceImpl(
    private val dao: CollectionDao,
) : CollectionsLocalSource {
    override fun observeAllWithStats(): Flow<List<CollectionWithStats>> = dao.observeAllWithStats()
    override suspend fun getById(id: Long): CollectionEntity? = dao.getById(id)
    override fun observeById(id: Long): Flow<CollectionEntity?> = dao.observeById(id)
    override suspend fun findByName(name: String): CollectionEntity? = dao.findByName(name)
    override suspend fun insert(entity: CollectionEntity): Long = dao.insert(entity)
    override suspend fun update(entity: CollectionEntity) = dao.update(entity)
    override suspend fun touchUpdatedAt(id: Long, timestamp: Long) = dao.touchUpdatedAt(id, timestamp)
    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}
