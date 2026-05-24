package com.domelabs.scanapp.feature.collections.impl.presentation.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.persistence.database.UnspecifiedCollection
import com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.CreateCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveCollectionsUseCase
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

class CollectionPickerViewModel(
    observeCollectionsUseCase: ObserveCollectionsUseCase,
    private val createCollectionUseCase: CreateCollectionUseCase,
) : ViewModel() {

    private val currentCollectionId = MutableStateFlow<Long?>(null)
    private val createMode = MutableStateFlow(false)
    private val newName = MutableStateFlow("")
    private val newColorHex = MutableStateFlow(CollectionPickerColors.palette.first())
    private val createError = MutableStateFlow<String?>(null)
    private val isSubmittingNewCollection = MutableStateFlow(false)

    private val pickedEvents = MutableSharedFlow<Long>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val pickedCollectionEvents: SharedFlow<Long> = pickedEvents.asSharedFlow()

    private val createFormState = combine(
        createMode,
        newName,
        newColorHex,
        createError,
        isSubmittingNewCollection,
    ) { mode, name, color, error, submitting ->
        CreateFormSnapshot(
            createMode = mode,
            newName = name,
            newColorHex = color,
            createError = error,
            isSubmittingNewCollection = submitting,
        )
    }

    val viewState: StateFlow<CollectionPickerViewState> = combine(
        observeCollectionsUseCase(),
        currentCollectionId,
        createFormState,
    ) { collections, currentId, form ->
        CollectionPickerViewState(
            collections = collections,
            currentCollectionId = currentId,
            createMode = form.createMode,
            newName = form.newName,
            newColorHex = form.newColorHex,
            availableColors = CollectionPickerColors.palette,
            createError = form.createError,
            isSubmittingNewCollection = form.isSubmittingNewCollection,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        CollectionPickerViewState(availableColors = CollectionPickerColors.palette),
    )

    fun setCurrentCollectionId(id: Long?) {
        currentCollectionId.value = id
    }

    fun onInteraction(interaction: CollectionPickerInteraction) {
        when (interaction) {
            is CollectionPickerInteraction.PickCollection -> viewModelScope.launch {
                pickedEvents.emit(interaction.collectionId)
            }

            CollectionPickerInteraction.EnterCreateMode -> {
                createMode.value = true
                createError.value = null
            }

            CollectionPickerInteraction.ExitCreateMode -> {
                resetCreateForm()
            }

            is CollectionPickerInteraction.UpdateNewName -> {
                newName.value = interaction.value.take(MAX_NAME_LENGTH)
                if (createError.value != null) createError.value = null
            }

            is CollectionPickerInteraction.SelectNewColor -> {
                newColorHex.value = interaction.colorHex
            }

            CollectionPickerInteraction.SubmitNewCollection -> submitNewCollection()
        }
    }

    private fun submitNewCollection() {
        if (isSubmittingNewCollection.value) return
        val name = newName.value.trim()
        val color = newColorHex.value
        if (name.isEmpty()) {
            createError.value = "Name cannot be empty"
            return
        }
        if (color.isEmpty()) {
            createError.value = "Pick a color"
            return
        }
        if (name.equals(UnspecifiedCollection.NAME, ignoreCase = true)) {
            createError.value = "Name is reserved"
            return
        }
        isSubmittingNewCollection.value = true
        viewModelScope.launch {
            val result = createCollectionUseCase(name = name, colorHex = color)
            isSubmittingNewCollection.value = false
            result.fold(
                onSuccess = { collection ->
                    resetCreateForm()
                    pickedEvents.emit(collection.id)
                },
                onFailure = { error ->
                    createError.value = when (error) {
                        CollectionsError.NameAlreadyExists -> "Name already exists"
                        else -> "Could not create collection"
                    }
                },
            )
        }
    }

    private fun resetCreateForm() {
        createMode.value = false
        newName.value = ""
        newColorHex.value = CollectionPickerColors.palette.first()
        createError.value = null
    }

    private data class CreateFormSnapshot(
        val createMode: Boolean,
        val newName: String,
        val newColorHex: String,
        val createError: String?,
        val isSubmittingNewCollection: Boolean,
    )

    companion object {
        const val MAX_NAME_LENGTH = 40
    }
}
