package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationDispatcher
import com.domelabs.scanapp.core.notification.AppConfirmationRequest
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.DeleteItemUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveCollectionsUseCase
import com.domelabs.scanapp.feature.collections.impl.domain.usecase.ObserveRecentItemsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ScanHistoryScreenViewModel(
    observeRecentItemsUseCase: ObserveRecentItemsUseCase,
    observeCollectionsUseCase: ObserveCollectionsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
) : ViewModel() {
    val viewState: StateFlow<ScanHistoryListViewState> = combine(
        observeRecentItemsUseCase(),
        observeCollectionsUseCase(),
    ) { items, collections ->
        val now = Clock.System.now().toEpochMilliseconds()
        val byId = collections.associateBy { it.collection.id }
        ScanHistoryListViewState(
            historyItems = items.map { it.toUi(now, byId) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ScanHistoryListViewState(),
    )

    fun onDelete(item: ScanHistoryItemUi) {
        AppConfirmationDispatcher.show(
            AppConfirmationRequest(
                title = "Delete item?",
                message = "\"${item.displayName}\" will be permanently removed.",
                confirmLabel = "Delete",
                onConfirm = {
                    deleteItemUseCase(item.id)
                    AppSnackbarDispatcher.dispatch(
                        AppSnackbarEvent(
                            title = "Item deleted",
                            kind = AppSnackbarKind.Success,
                            durationMillis = 3_500L,
                        )
                    )
                },
            )
        )
    }

    fun onBack() {
        viewModelScope.launch {
            NavigationDispatcher.back()
        }
    }

    fun openDetails(item: ScanHistoryItemUi) {
        viewModelScope.launch {
            NavigationDispatcher.navigate(
                NavRoute.ItemDetails(id = item.id)
            )
        }
    }
}
