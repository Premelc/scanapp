package com.domelabs.scanapp.feature.collections.impl.presentation.create

sealed interface CreateCollectionInteraction {
    data class UpdateName(val value: String) : CreateCollectionInteraction
    data class SelectColor(val colorHex: String) : CreateCollectionInteraction
    data object Submit : CreateCollectionInteraction
}
