package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository

class RenameCollectionUseCase(
    private val repository: CollectionsRepository,
) {
    suspend operator fun invoke(id: Long, newName: String): Result<Collection> = runCatching {
        repository.rename(id = id, newName = newName.trim())
    }
}
