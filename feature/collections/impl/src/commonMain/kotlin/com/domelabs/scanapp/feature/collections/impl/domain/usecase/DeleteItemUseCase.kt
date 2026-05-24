package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository

class DeleteItemUseCase(
    private val repository: ScannedItemsRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteById(id)
    }
}
