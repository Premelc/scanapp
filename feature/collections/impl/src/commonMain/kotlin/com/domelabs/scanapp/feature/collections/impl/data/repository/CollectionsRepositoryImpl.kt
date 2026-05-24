package com.domelabs.scanapp.feature.collections.impl.data.repository

import com.domelabs.scanapp.core.persistence.database.UnspecifiedCollection
import com.domelabs.scanapp.core.persistence.database.entity.CollectionEntity
import com.domelabs.scanapp.feature.collections.impl.data.mapper.toDomain
import com.domelabs.scanapp.feature.collections.impl.data.source.CollectionsLocalSource
import com.domelabs.scanapp.feature.collections.impl.data.source.ScannedItemsLocalSource
import com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError
import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CollectionsRepositoryImpl(
    private val collectionsSource: CollectionsLocalSource,
    private val itemsSource: ScannedItemsLocalSource,
) : CollectionsRepository {

    override fun observeAll(): Flow<List<CollectionSummary>> = flow {
        ensureUnspecifiedExists()
        collectionsSource.observeAllWithStats().collect { rows ->
            emit(rows.map { it.toDomain() })
        }
    }

    override suspend fun getById(id: Long): Collection? =
        collectionsSource.getById(id)?.toDomain()

    override fun observeCollectionById(id: Long): Flow<Collection?> =
        collectionsSource.observeById(id).map { entity -> entity?.toDomain() }

    override suspend fun create(name: String, colorHex: String): Collection {
        ensureUnspecifiedExists()
        if (name.isBlank() || name.equals(UnspecifiedCollection.NAME, ignoreCase = true)) {
            throw CollectionsError.NameAlreadyExists
        }
        if (collectionsSource.findByName(name) != null) throw CollectionsError.NameAlreadyExists
        val now = Clock.System.now().toEpochMilliseconds()
        val id = collectionsSource.insert(
            CollectionEntity(
                name = name,
                colorHex = colorHex,
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now,
            )
        )
        return requireNotNull(collectionsSource.getById(id)).toDomain()
    }

    override suspend fun rename(id: Long, newName: String): Collection {
        val current = collectionsSource.getById(id) ?: throw CollectionsError.CollectionNotFound
        if (current.isUnspecifiedCollection()) throw CollectionsError.CannotEditUnspecified
        if (newName.isBlank() || newName.equals(UnspecifiedCollection.NAME, ignoreCase = true)) {
            throw CollectionsError.NameAlreadyExists
        }
        val existing = collectionsSource.findByName(newName)
        if (existing != null && existing.id != id) throw CollectionsError.NameAlreadyExists
        collectionsSource.update(current.copy(name = newName))
        return requireNotNull(collectionsSource.getById(id)).toDomain()
    }

    override suspend fun recolor(id: Long, colorHex: String): Collection {
        val current = collectionsSource.getById(id) ?: throw CollectionsError.CollectionNotFound
        if (current.isUnspecifiedCollection()) throw CollectionsError.CannotEditUnspecified
        collectionsSource.update(current.copy(colorHex = colorHex))
        return requireNotNull(collectionsSource.getById(id)).toDomain()
    }

    override suspend fun delete(id: Long, cascadeDeleteItems: Boolean) {
        val current = collectionsSource.getById(id) ?: throw CollectionsError.CollectionNotFound
        if (current.isUnspecifiedCollection()) throw CollectionsError.CannotEditUnspecified
        if (cascadeDeleteItems) {
            itemsSource.deleteAllInCollection(id)
        } else {
            val unspecifiedId = resolveUnspecifiedCollectionId()
            itemsSource.reassignAllInCollection(
                sourceCollectionId = id,
                targetCollectionId = unspecifiedId,
            )
            collectionsSource.touchUpdatedAt(
                id = unspecifiedId,
                timestamp = Clock.System.now().toEpochMilliseconds(),
            )
        }
        collectionsSource.deleteById(id)
    }

    override suspend fun touch(id: Long, timestampEpochMillis: Long) {
        collectionsSource.touchUpdatedAt(id = id, timestamp = timestampEpochMillis)
    }

    override suspend fun ensureUnspecifiedExists() {
        if (collectionsSource.findByName(UnspecifiedCollection.NAME) != null) return

        val now = Clock.System.now().toEpochMilliseconds()
        val atReservedId = collectionsSource.getById(UnspecifiedCollection.ID)
        if (atReservedId == null) {
            collectionsSource.insert(
                CollectionEntity(
                    id = UnspecifiedCollection.ID,
                    name = UnspecifiedCollection.NAME,
                    colorHex = UnspecifiedCollection.COLOR_HEX,
                    createdAtEpochMillis = now,
                    updatedAtEpochMillis = now,
                )
            )
        } else {
            collectionsSource.insert(
                CollectionEntity(
                    name = UnspecifiedCollection.NAME,
                    colorHex = UnspecifiedCollection.COLOR_HEX,
                    createdAtEpochMillis = now,
                    updatedAtEpochMillis = now,
                )
            )
        }
    }

    override suspend fun resolveUnspecifiedCollectionId(): Long {
        ensureUnspecifiedExists()
        return collectionsSource.findByName(UnspecifiedCollection.NAME)?.id
            ?: throw CollectionsError.CollectionNotFound
    }
}
