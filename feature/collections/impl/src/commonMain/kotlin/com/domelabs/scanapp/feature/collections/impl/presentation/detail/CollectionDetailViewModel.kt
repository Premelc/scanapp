package com.domelabs.scanapp.feature.collections.impl.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationRequest
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
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
        CollectionDetailViewState(
            collection = collection,
            items = items,
            searchQuery = query,
            isSearching = query.isNotBlank(),
            showOverflowMenu = overflow,
            editForm = form,
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

            CollectionDetailInteraction.OpenDeleteConfirmation -> requestDeleteConfirmation()
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
            AppSnackbarDispatcher.dispatch(
                AppSnackbarEvent(
                    title = "Collection updated",
                    kind = AppSnackbarKind.Success,
                    durationMillis = 3_500L,
                )
            )
        }
    }

    private fun requestDeleteConfirmation() {
        val current = viewState.value.collection ?: return
        showOverflowMenu.value = false
        AppConfirmationDispatcher.show(
            AppConfirmationRequest(
                title = "Delete \"${current.name}\"?",
                message = "Choose what to do with items in this collection.",
                confirmLabel = "Delete items too",
                secondaryConfirmLabel = "Move to Unspecified",
                onConfirm = { deleteCollection(cascade = true) },
                onSecondaryConfirm = { deleteCollection(cascade = false) },
            )
        )
    }

    private suspend fun deleteCollection(cascade: Boolean) {
        val current = viewState.value.collection ?: return
        deleteCollectionUseCase(current.id, cascadeDeleteItems = cascade)
        AppSnackbarDispatcher.dispatch(
            AppSnackbarEvent(
                title = "Collection deleted",
                kind = AppSnackbarKind.Success,
                durationMillis = 3_500L,
            )
        )
        NavigationDispatcher.back()
    }

    private fun mapErrorMessage(throwable: Throwable?): String = when (throwable) {
        CollectionsError.NameAlreadyExists -> "Name already exists"
        CollectionsError.CannotEditUnspecified -> "Unspecified can't be edited"
        CollectionsError.CollectionNotFound -> "Collection no longer exists"
        else -> "Something went wrong"
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MILLIS = 300L
        const val MAX_NAME_LENGTH = 40
    }
}
