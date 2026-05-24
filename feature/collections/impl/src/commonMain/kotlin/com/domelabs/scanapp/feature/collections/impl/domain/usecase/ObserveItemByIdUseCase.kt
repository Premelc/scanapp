package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository
import kotlinx.coroutines.flow.Flow

class ObserveItemByIdUseCase(
    private val repository: ScannedItemsRepository,
) {
    operator fun invoke(id: Long): Flow<ScannedItem?> = repository.observeById(id)
}
