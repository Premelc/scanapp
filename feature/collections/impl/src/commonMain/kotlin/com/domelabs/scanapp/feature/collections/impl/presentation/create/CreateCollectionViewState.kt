package com.domelabs.scanapp.feature.collections.impl.presentation.create

data class CreateCollectionViewState(
    val name: String = "",
    val colorHex: String = "",
    val availableColors: List<String> = emptyList(),
    val error: String? = null,
    val isSubmitting: Boolean = false,
) {
    val canSubmit: Boolean
        get() = name.trim().isNotEmpty() && colorHex.isNotEmpty() && !isSubmitting
}
