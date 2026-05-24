package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
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

    fun onDelete(id: Long) {
        viewModelScope.launch {
            deleteItemUseCase(id)
        }
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
