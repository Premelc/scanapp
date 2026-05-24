package com.domelabs.scanapp.feature.collections.impl.presentation.list

sealed interface CollectionsListInteraction {
    data object Back : CollectionsListInteraction
    data class OpenCollection(val id: Long) : CollectionsListInteraction
    data object OpenCreateSheet : CollectionsListInteraction
    data object DismissCreateSheet : CollectionsListInteraction
}
