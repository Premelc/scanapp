package com.domelabs.scanapp.feature.collections.impl.presentation.picker

import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary

data class CollectionPickerViewState(
    val collections: List<CollectionSummary> = emptyList(),
    val currentCollectionId: Long? = null,
    val createMode: Boolean = false,
    val newName: String = "",
    val newColorHex: String = "",
    val availableColors: List<String> = emptyList(),
    val createError: String? = null,
    val isSubmittingNewCollection: Boolean = false,
) {
    val canSubmitNewCollection: Boolean
        get() = createMode &&
            newName.trim().isNotEmpty() &&
            newColorHex.isNotEmpty() &&
            !isSubmittingNewCollection
}
