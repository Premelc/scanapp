package com.domelabs.scanapp.feature.collections.impl.presentation.detail

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem

data class CollectionDetailViewState(
    val collection: Collection? = null,
    val items: List<ScannedItem> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val showOverflowMenu: Boolean = false,
    val editForm: EditCollectionForm? = null,
    val showDeleteDialog: Boolean = false,
    val availableColors: List<String> = emptyList(),
)

data class EditCollectionForm(
    val name: String,
    val colorHex: String,
    val error: String? = null,
    val isSubmitting: Boolean = false,
)
