package com.domelabs.scanapp.feature.collections.impl.domain.repository

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItemSource
import kotlinx.coroutines.flow.Flow

interface ScannedItemsRepository {
    fun observeRecent(limit: Int): Flow<List<ScannedItem>>

    fun observeByCollection(collectionId: Long): Flow<List<ScannedItem>>

    fun searchInCollection(collectionId: Long, query: String): Flow<List<ScannedItem>>

    suspend fun getById(id: Long): ScannedItem?

    fun observeById(id: Long): Flow<ScannedItem?>

    /**
     * Registers a freshly scanned code. Subject to the 10-second dedupe rule
     * across all collections (matches on [rawValue]).
     *
     * @return the inserted item, or null when the insertion was suppressed by
     *         the dedupe rule.
     */
    suspend fun register(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScannedItemSource,
    ): ScannedItem?

    /**
     * Moves an item to [targetCollectionId] and bumps the target collection's
     * `updatedAt`. The source collection's `updatedAt` is not changed.
     */
    suspend fun moveToCollection(itemId: Long, targetCollectionId: Long)

    suspend fun setCustomName(itemId: Long, customName: String?)

    suspend fun deleteById(id: Long)
}
