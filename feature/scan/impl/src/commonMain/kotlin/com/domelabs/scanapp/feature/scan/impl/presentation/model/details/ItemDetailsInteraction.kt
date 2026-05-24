package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

sealed interface ItemDetailsInteraction {
    data object Back : ItemDetailsInteraction
    data object Delete : ItemDetailsInteraction
    data object Share : ItemDetailsInteraction
    data object DismissShare : ItemDetailsInteraction
    data object OpenCollectionPicker : ItemDetailsInteraction
    data object DismissCollectionPicker : ItemDetailsInteraction
    data class MoveToCollection(val collectionId: Long) : ItemDetailsInteraction
    data class UpdateCustomNameDraft(val value: String) : ItemDetailsInteraction
    data object CommitCustomName : ItemDetailsInteraction
}
