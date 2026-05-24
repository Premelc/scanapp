package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationRequest
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.repository.CollectionsRepository
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.DeleteItemUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.MoveItemToCollectionUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveItemByIdUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.RenameItemUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailsViewModel(
    val id: Long,
    observeItemByIdUseCase: ObserveItemByIdUseCase,
    private val collectionsRepository: CollectionsRepository,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val moveItemToCollectionUseCase: MoveItemToCollectionUseCase,
    private val renameItemUseCase: RenameItemUseCase,
) : ViewModel() {
    private val showShareSheet = MutableStateFlow(false)
    private val showCollectionPicker = MutableStateFlow(false)
    private val customNameDraft = MutableStateFlow<String?>(null)

    private val itemFlow = observeItemByIdUseCase(id)
    private val collectionFlow = itemFlow.flatMapLatest { item ->
        if (item == null) flowOf<Collection?>(null)
        else collectionsRepository.observeCollectionById(item.collectionId)
    }

    val viewState: StateFlow<ItemDetailsViewState> = combine(
        itemFlow,
        collectionFlow,
        showShareSheet,
        showCollectionPicker,
        customNameDraft,
    ) { item, collection, share, picker, draft ->
        ItemDetailsViewState(
            item = item,
            collection = collection,
            customNameDraft = draft ?: item?.customName.orEmpty(),
            showShareSheet = share,
            showCollectionPicker = picker,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        ItemDetailsViewState(),
    )

    fun onInteraction(interaction: ItemDetailsInteraction) {
        when (interaction) {
            ItemDetailsInteraction.Back -> viewModelScope.launch {
                NavigationDispatcher.back()
            }

            ItemDetailsInteraction.Delete -> requestDeleteConfirmation()

            ItemDetailsInteraction.Share -> {
                showShareSheet.value = true
            }

            ItemDetailsInteraction.DismissShare -> {
                showShareSheet.value = false
            }

            ItemDetailsInteraction.OpenCollectionPicker -> {
                showCollectionPicker.value = true
            }

            ItemDetailsInteraction.DismissCollectionPicker -> {
                showCollectionPicker.value = false
            }

            is ItemDetailsInteraction.MoveToCollection -> {
                showCollectionPicker.value = false
                viewModelScope.launch {
                    moveItemToCollectionUseCase(id, interaction.collectionId)
                    val collectionName = collectionsRepository.getById(interaction.collectionId)?.name
                        ?: "collection"
                    AppSnackbarDispatcher.dispatch(
                        AppSnackbarEvent(
                            title = "Moved to $collectionName",
                            kind = AppSnackbarKind.Success,
                            durationMillis = 3_500L,
                        )
                    )
                }
            }

            is ItemDetailsInteraction.UpdateCustomNameDraft -> {
                val trimmed = interaction.value.take(ItemDetailsConstants.CUSTOM_NAME_LIMIT)
                customNameDraft.value = trimmed
            }

            ItemDetailsInteraction.CommitCustomName -> {
                val draft = customNameDraft.value ?: return
                val normalized = draft.trim().ifEmpty { null }
                viewModelScope.launch {
                    renameItemUseCase(id, normalized)
                    customNameDraft.value = null
                    AppSnackbarDispatcher.dispatch(
                        AppSnackbarEvent(
                            title = if (normalized == null) "Custom name cleared" else "Custom name saved",
                            kind = AppSnackbarKind.Success,
                            durationMillis = 3_500L,
                        )
                    )
                }
            }
        }
    }

    private fun requestDeleteConfirmation() {
        val item = viewState.value.item
        val label = item?.customName ?: item?.rawValue ?: "this item"
        AppConfirmationDispatcher.show(
            AppConfirmationRequest(
                title = "Delete item?",
                message = "\"$label\" will be permanently removed.",
                confirmLabel = "Delete",
                onConfirm = {
                    deleteItemUseCase(id)
                    AppSnackbarDispatcher.dispatch(
                        AppSnackbarEvent(
                            title = "Item deleted",
                            kind = AppSnackbarKind.Success,
                            durationMillis = 3_500L,
                        )
                    )
                    NavigationDispatcher.back()
                },
            )
        )
    }
}
