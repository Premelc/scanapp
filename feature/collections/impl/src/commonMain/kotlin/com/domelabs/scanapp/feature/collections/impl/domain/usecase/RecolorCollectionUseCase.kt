package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository

class RecolorCollectionUseCase(
    private val repository: CollectionsRepository,
) {
    suspend operator fun invoke(id: Long, colorHex: String): Result<Collection> = runCatching {
        repository.recolor(id = id, colorHex = colorHex)
    }
}
