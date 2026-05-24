package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository
import kotlinx.coroutines.flow.Flow

class SearchItemsInCollectionUseCase(
    private val repository: ScannedItemsRepository,
) {
    operator fun invoke(collectionId: Long, query: String): Flow<List<ScannedItem>> =
        repository.searchInCollection(collectionId, query)
}
