package com.domelabs.scanapp.feature.collections.impl.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.feature.collections.impl.domain.error.CollectionsError
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.DeleteCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveItemsInCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RecolorCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RenameCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.CollectionPickerColors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class CollectionDetailViewModel(
    val id: Long,
    private val collectionsRepository: CollectionsRepository,
    private val observeItemsInCollectionUseCase: ObserveItemsInCollectionUseCase,
    private val renameCollectionUseCase: RenameCollectionUseCase,
    private val recolorCollectionUseCase: RecolorCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val showOverflowMenu = MutableStateFlow(false)
    private val editForm = MutableStateFlow<EditCollectionForm?>(null)
    private val showDeleteDialog = MutableStateFlow(false)

    private val collectionFlow = collectionsRepository.observeCollectionById(id)

    private val itemsFlow = searchQuery
        .debounce { q -> if (q.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            observeItemsInCollectionUseCase(id, query)
        }

    val viewState: StateFlow<CollectionDetailViewState> = combine(
        collectionFlow,
        itemsFlow,
        searchQuery,
        showOverflowMenu,
        editForm,
    ) { collection, items, query, overflow, form ->
        Snapshot(collection, items, query, overflow, form)
    }.combine(showDeleteDialog) { snap, deleteDialog ->
        CollectionDetailViewState(
            collection = snap.collection,
            items = snap.items,
            searchQuery = snap.query,
            isSearching = snap.query.isNotBlank(),
            showOverflowMenu = snap.overflow,
            editForm = snap.form,
            showDeleteDialog = deleteDialog,
            availableColors = CollectionPickerColors.palette,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        CollectionDetailViewState(availableColors = CollectionPickerColors.palette),
    )

    fun onInteraction(interaction: CollectionDetailInteraction) {
        when (interaction) {
            CollectionDetailInteraction.Back -> viewModelScope.launch {
                NavigationDispatcher.back()
            }

            is CollectionDetailInteraction.OpenItem -> viewModelScope.launch {
                NavigationDispatcher.navigate(NavRoute.ItemDetails(id = interaction.id))
            }

            is CollectionDetailInteraction.UpdateSearchQuery -> {
                searchQuery.value = interaction.value
            }

            CollectionDetailInteraction.ClearSearchQuery -> {
                searchQuery.value = ""
            }

            CollectionDetailInteraction.OpenOverflowMenu -> {
                showOverflowMenu.value = true
            }

            CollectionDetailInteraction.DismissOverflowMenu -> {
                showOverflowMenu.value = false
            }

            CollectionDetailInteraction.OpenEditSheet -> openEditSheet()
            CollectionDetailInteraction.DismissEditSheet -> {
                editForm.value = null
            }

            is CollectionDetailInteraction.UpdateEditName -> {
                editForm.value = editForm.value?.copy(
                    name = interaction.value.take(MAX_NAME_LENGTH),
                    error = null,
                )
            }

            is CollectionDetailInteraction.SelectEditColor -> {
                editForm.value = editForm.value?.copy(colorHex = interaction.colorHex)
            }

            CollectionDetailInteraction.SubmitEdit -> submitEdit()

            CollectionDetailInteraction.OpenDeleteDialog -> {
                showOverflowMenu.value = false
                showDeleteDialog.value = true
            }

            CollectionDetailInteraction.DismissDeleteDialog -> {
                showDeleteDialog.value = false
            }

            CollectionDetailInteraction.ConfirmDeleteAndCascade -> deleteCollection(cascade = true)
            CollectionDetailInteraction.ConfirmDeleteAndMoveToUnspecified -> deleteCollection(cascade = false)
        }
    }

    private fun openEditSheet() {
        val current = viewState.value.collection ?: return
        showOverflowMenu.value = false
        editForm.value = EditCollectionForm(
            name = current.name,
            colorHex = current.colorHex,
        )
    }

    private fun submitEdit() {
        val current = viewState.value.collection ?: return
        val form = editForm.value ?: return
        if (form.isSubmitting) return
        val newName = form.name.trim()
        val newColor = form.colorHex
        if (newName.isEmpty()) {
            editForm.value = form.copy(error = "Name cannot be empty")
            return
        }
        editForm.value = form.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            if (newName != current.name) {
                val result = renameCollectionUseCase(current.id, newName)
                if (result.isFailure) {
                    editForm.value = editForm.value?.copy(
                        error = mapErrorMessage(result.exceptionOrNull()),
                        isSubmitting = false,
                    )
                    return@launch
                }
            }
            if (newColor.isNotEmpty() && newColor != current.colorHex) {
                val result = recolorCollectionUseCase(current.id, newColor)
                if (result.isFailure) {
                    editForm.value = editForm.value?.copy(
                        error = mapErrorMessage(result.exceptionOrNull()),
                        isSubmitting = false,
                    )
                    return@launch
                }
            }
            editForm.value = null
        }
    }

    private fun deleteCollection(cascade: Boolean) {
        val current = viewState.value.collection ?: return
        viewModelScope.launch {
            deleteCollectionUseCase(current.id, cascadeDeleteItems = cascade)
            showDeleteDialog.value = false
            NavigationDispatcher.back()
        }
    }

    private fun mapErrorMessage(throwable: Throwable?): String = when (throwable) {
        CollectionsError.NameAlreadyExists -> "Name already exists"
        CollectionsError.CannotEditUnspecified -> "Unspecified can't be edited"
        CollectionsError.CollectionNotFound -> "Collection no longer exists"
        else -> "Something went wrong"
    }

    private data class Snapshot(
        val collection: com.domelabs.scanapp.feature.collections.impl.domain.model.Collection?,
        val items: List<com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem>,
        val query: String,
        val overflow: Boolean,
        val form: EditCollectionForm?,
    )

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val MAX_NAME_LENGTH = 40
    }
}
