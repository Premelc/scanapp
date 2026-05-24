package com.domelabs.scanapp.feature.collections.impl.data.repository

import com.domelabs.scanapp.core.persistence.database.entity.ScannedItemEntity
import com.domelabs.scanapp.feature.collections.impl.data.mapper.toDomain
import com.domelabs.scanapp.feature.collections.impl.data.source.CollectionsLocalSource
import com.domelabs.scanapp.feature.collections.impl.data.source.ScannedItemsLocalSource
import com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItemSource
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ScannedItemsRepositoryImpl(
    private val itemsSource: ScannedItemsLocalSource,
    private val collectionsSource: CollectionsLocalSource,
    private val collectionsRepository: CollectionsRepository,
) : com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository {

    override fun observeRecent(limit: Int): Flow<List<ScannedItem>> =
        itemsSource.observeRecent(limit).map { rows -> rows.map { it.toDomain() } }

    override fun observeByCollection(collectionId: Long): Flow<List<ScannedItem>> =
        itemsSource.observeByCollection(collectionId).map { rows -> rows.map { it.toDomain() } }

    override fun searchInCollection(collectionId: Long, query: String): Flow<List<ScannedItem>> =
        itemsSource.searchInCollection(collectionId, query).map { rows -> rows.map { it.toDomain() } }

    override suspend fun getById(id: Long): ScannedItem? =
        itemsSource.getById(id)?.toDomain()

    override fun observeById(id: Long): Flow<ScannedItem?> =
        itemsSource.observeById(id).map { entity -> entity?.toDomain() }

    override suspend fun register(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScannedItemSource,
    ): ScannedItem? {
        val now = Clock.System.now().toEpochMilliseconds()
        val latest = itemsSource.getLatestByRawValue(rawValue)
        if (latest != null && (now - latest.timestampEpochMillis) < DEDUPE_WINDOW_MILLIS) {
            return null
        }
        collectionsRepository.ensureUnspecifiedExists()
        val unspecifiedId = collectionsRepository.resolveUnspecifiedCollectionId()
        val entity = ScannedItemEntity(
            collectionId = unspecifiedId,
            timestampEpochMillis = now,
            rawValue = rawValue,
            codeKind = codeKind,
            codeFormat = codeFormat,
            source = source.name,
        )
        val id = itemsSource.insert(entity)
        collectionsSource.touchUpdatedAt(id = unspecifiedId, timestamp = now)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun moveToCollection(itemId: Long, targetCollectionId: Long) {
        val item = itemsSource.getById(itemId) ?: throw CollectionsError.ItemNotFound
        if (collectionsSource.getById(targetCollectionId) == null) {
            throw CollectionsError.CollectionNotFound
        }
        if (item.collectionId == targetCollectionId) return
        itemsSource.setCollection(id = itemId, targetCollectionId = targetCollectionId)
        collectionsSource.touchUpdatedAt(
            id = targetCollectionId,
            timestamp = Clock.System.now().toEpochMilliseconds(),
        )
    }

    override suspend fun setCustomName(itemId: Long, customName: String?) {
        itemsSource.setCustomName(id = itemId, customName = customName)
    }

    override suspend fun deleteById(id: Long) {
        itemsSource.deleteById(id)
    }

    private companion object {
        const val DEDUPE_WINDOW_MILLIS = 10_000L
    }
}
