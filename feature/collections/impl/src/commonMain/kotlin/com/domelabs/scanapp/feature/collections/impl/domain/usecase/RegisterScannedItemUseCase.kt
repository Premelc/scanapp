package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItemSource
import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository

/**
 * Registers a freshly scanned code into the data layer. Returns `null` when
 * the scan is suppressed by the 10-second dedupe rule.
 *
 * Exposed via `feature:collections:api` for consumption by `feature:scan:impl`.
 */
class RegisterScannedItemUseCase(
    private val repository: ScannedItemsRepository,
) {
    suspend operator fun invoke(
        rawValue: String,
        codeKind: String,
        codeFormat: String,
        source: ScannedItemSource,
    ): ScannedItem? = repository.register(
        rawValue = rawValue,
        codeKind = codeKind,
        codeFormat = codeFormat,
        source = source,
    )
}
