package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository

class MoveItemToCollectionUseCase(
    private val repository: ScannedItemsRepository,
) {
    suspend operator fun invoke(itemId: Long, targetCollectionId: Long) {
        repository.moveToCollection(itemId = itemId, targetCollectionId = targetCollectionId)
    }
}
