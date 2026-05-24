package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository
import kotlinx.coroutines.flow.Flow

class ObserveItemsInCollectionUseCase(
    private val repository: ScannedItemsRepository,
) {
    /**
     * Returns items in [collectionId] filtered by [query]. When [query] is
     * blank, returns all items in the collection.
     */
    operator fun invoke(collectionId: Long, query: String): Flow<List<ScannedItem>> =
        if (query.isBlank()) {
            repository.observeByCollection(collectionId)
        } else {
            repository.searchInCollection(collectionId, query.trim())
        }
}
