package com.domelabs.scanapp.feature.collections.impl.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.persistence.database.UnspecifiedCollection
import com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.CreateCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.CollectionPickerColors
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateCollectionViewModel(
    private val createCollectionUseCase: CreateCollectionUseCase,
) : ViewModel() {

    private val name = MutableStateFlow("")
    private val colorHex = MutableStateFlow(CollectionPickerColors.palette.first())
    private val error = MutableStateFlow<String?>(null)
    private val isSubmitting = MutableStateFlow(false)

    private val createdEventsMutable = MutableSharedFlow<Long>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val createdEvents: SharedFlow<Long> = createdEventsMutable.asSharedFlow()

    val viewState: StateFlow<CreateCollectionViewState> = combine(
        name,
        colorHex,
        error,
        isSubmitting,
    ) { n, c, e, sub ->
        CreateCollectionViewState(
            name = n,
            colorHex = c,
            availableColors = CollectionPickerColors.palette,
            error = e,
            isSubmitting = sub,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        CreateCollectionViewState(
            availableColors = CollectionPickerColors.palette,
            colorHex = CollectionPickerColors.palette.first(),
        ),
    )

    fun onInteraction(interaction: CreateCollectionInteraction) {
        when (interaction) {
            is CreateCollectionInteraction.UpdateName -> {
                name.value = interaction.value.take(MAX_NAME_LENGTH)
                if (error.value != null) error.value = null
            }

            is CreateCollectionInteraction.SelectColor -> {
                colorHex.value = interaction.colorHex
            }

            CreateCollectionInteraction.Submit -> submit()
        }
    }

    private fun submit() {
        if (isSubmitting.value) return
        val trimmed = name.value.trim()
        val color = colorHex.value
        if (trimmed.isEmpty()) {
            error.value = "Name cannot be empty"
            return
        }
        if (color.isEmpty()) {
            error.value = "Pick a color"
            return
        }
        if (trimmed.equals(UnspecifiedCollection.NAME, ignoreCase = true)) {
            error.value = "Name is reserved"
            return
        }
        isSubmitting.value = true
        viewModelScope.launch {
            val result = createCollectionUseCase(name = trimmed, colorHex = color)
            isSubmitting.value = false
            result.fold(
                onSuccess = { created ->
                    name.value = ""
                    error.value = null
                    createdEventsMutable.emit(created.id)
                },
                onFailure = { failure ->
                    error.value = when (failure) {
                        CollectionsError.NameAlreadyExists -> "Name already exists"
                        else -> "Could not create collection"
                    }
                },
            )
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 40
    }
}
