package com.domelabs.scanapp.feature.collections.impl.domain.repository

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import kotlinx.coroutines.flow.Flow

interface CollectionsRepository {
    fun observeAll(): Flow<List<CollectionSummary>>

    suspend fun getById(id: Long): Collection?

    fun observeCollectionById(id: Long): Flow<Collection?>

    /** @throws com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError.NameAlreadyExists */
    suspend fun create(name: String, colorHex: String): Collection

    /** @throws CollectionsError.NameAlreadyExists, CannotEditUnspecified, CollectionNotFound */
    suspend fun rename(id: Long, newName: String): Collection

    /** @throws CollectionsError.CannotEditUnspecified, CollectionNotFound */
    suspend fun recolor(id: Long, colorHex: String): Collection

    /**
     * Deletes the collection. If [cascadeDeleteItems] is true, items in the
     * collection are deleted with it. Otherwise items are reassigned to the
     * Unspecified collection before the deletion.
     *
     * @throws com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError.CannotEditUnspecified
     */
    suspend fun delete(id: Long, cascadeDeleteItems: Boolean)

    /** Bumps the collection's `updatedAt` to [timestampEpochMillis]. */
    suspend fun touch(id: Long, timestampEpochMillis: Long)

    /** Idempotently ensures the system-managed Unspecified row exists. */
    suspend fun ensureUnspecifiedExists()

    /** Returns the persisted id of the Unspecified collection (after [ensureUnspecifiedExists]). */
    suspend fun resolveUnspecifiedCollectionId(): Long
}
