package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectionsUseCase(
    private val repository: CollectionsRepository,
) {
    operator fun invoke(): Flow<List<CollectionSummary>> = repository.observeAll()
}
