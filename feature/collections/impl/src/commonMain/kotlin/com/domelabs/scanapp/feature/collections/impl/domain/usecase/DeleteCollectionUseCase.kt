package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository

class DeleteCollectionUseCase(
    private val repository: CollectionsRepository,
) {
    /**
     * @param cascadeDeleteItems when true, items in the collection are deleted
     *        together with the collection. When false, items are reassigned to
     *        the Unspecified collection first.
     */
    suspend operator fun invoke(id: Long, cascadeDeleteItems: Boolean): Result<Unit> = runCatching {
        repository.delete(id = id, cascadeDeleteItems = cascadeDeleteItems)
    }
}
