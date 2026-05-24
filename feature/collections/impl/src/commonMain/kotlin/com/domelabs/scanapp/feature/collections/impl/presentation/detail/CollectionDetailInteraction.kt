package com.domelabs.scanapp.feature.collections.impl.presentation.detail

sealed interface CollectionDetailInteraction {
    data object Back : CollectionDetailInteraction

    data class OpenItem(val id: Long) : CollectionDetailInteraction
    data class UpdateSearchQuery(val value: String) : CollectionDetailInteraction
    data object ClearSearchQuery : CollectionDetailInteraction

    data object OpenOverflowMenu : CollectionDetailInteraction
    data object DismissOverflowMenu : CollectionDetailInteraction

    data object OpenEditSheet : CollectionDetailInteraction
    data object DismissEditSheet : CollectionDetailInteraction
    data class UpdateEditName(val value: String) : CollectionDetailInteraction
    data class SelectEditColor(val colorHex: String) : CollectionDetailInteraction
    data object SubmitEdit : CollectionDetailInteraction

    data object OpenDeleteDialog : CollectionDetailInteraction
    data object DismissDeleteDialog : CollectionDetailInteraction
    data object ConfirmDeleteAndCascade : CollectionDetailInteraction
    data object ConfirmDeleteAndMoveToUnspecified : CollectionDetailInteraction
}
