package com.domelabs.scanapp.feature.collections.impl.presentation.picker

sealed interface CollectionPickerInteraction {
    data class PickCollection(val collectionId: Long) : CollectionPickerInteraction
    data object EnterCreateMode : CollectionPickerInteraction
    data object ExitCreateMode : CollectionPickerInteraction
    data class UpdateNewName(val value: String) : CollectionPickerInteraction
    data class SelectNewColor(val colorHex: String) : CollectionPickerInteraction
    data object SubmitNewCollection : CollectionPickerInteraction
}
