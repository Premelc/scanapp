package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository

class GetItemByIdUseCase(
    private val repository: ScannedItemsRepository,
) {
    suspend operator fun invoke(id: Long): ScannedItem? = repository.getById(id)
}
