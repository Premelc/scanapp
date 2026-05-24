package com.domelabs.scanapp.feature.collections.impl.presentation.list

data class CollectionsListViewState(
    val rows: List<CollectionRowUi> = emptyList(),
    val showCreateSheet: Boolean = false,
)

data class CollectionRowUi(
    val id: Long,
    val name: String,
    val colorHex: String,
    val itemCount: Int,
    val itemCountLabel: String,
    val lastUpdatedLabel: String,
    val isSystemManaged: Boolean,
)
