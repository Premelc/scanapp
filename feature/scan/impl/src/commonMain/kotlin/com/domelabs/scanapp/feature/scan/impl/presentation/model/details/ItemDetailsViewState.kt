package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem

data class ItemDetailsViewState(
    val item: ScannedItem? = null,
    val collection: Collection? = null,
    val customNameDraft: String = "",
    val showShareSheet: Boolean = false,
    val showCollectionPicker: Boolean = false,
) {
    val customNameLimit: Int get() = ItemDetailsConstants.CUSTOM_NAME_LIMIT
}

object ItemDetailsConstants {
    const val CUSTOM_NAME_LIMIT: Int = 60
}
