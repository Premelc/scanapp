package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository
import kotlinx.coroutines.flow.Flow

class ObserveRecentItemsUseCase(
    private val repository: ScannedItemsRepository,
) {
    operator fun invoke(limit: Int = DEFAULT_LIMIT): Flow<List<ScannedItem>> =
        repository.observeRecent(limit)

    companion object {
        const val DEFAULT_LIMIT = 50
    }
}
