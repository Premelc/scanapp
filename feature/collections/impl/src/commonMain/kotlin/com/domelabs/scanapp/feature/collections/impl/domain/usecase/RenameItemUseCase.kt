package com.domelabs.scanapp.feature.collections.impl.domain.usecase

import com.domelabs.scanapp.feature.collections.impl.domain.repository.ScannedItemsRepository

class RenameItemUseCase(
    private val repository: ScannedItemsRepository,
) {
    /**
     * Sets or clears an item's custom display name.
     *
     * @param customName when null or blank, the name is cleared and the item
     *        falls back to its raw value for display.
     */
    suspend operator fun invoke(itemId: Long, customName: String?) {
        val normalized = customName?.trim()?.takeIf { it.isNotEmpty() }
        repository.setCustomName(itemId = itemId, customName = normalized)
    }

    companion object {
        const val MAX_NAME_LENGTH = 60
    }
}
