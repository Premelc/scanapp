package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository

class CreateCollectionUseCase(
    private val repository: CollectionsRepository,
) {
    /**
     * @return the created collection on success, or a [Result.failure] holding
     *         a [com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError]
     *         on validation failure.
     */
    suspend operator fun invoke(name: String, colorHex: String): Result<Collection> = runCatching {
        repository.create(name = name.trim(), colorHex = colorHex)
    }
}
