package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ClearScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.DeleteScanHistoryItemUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ObserveScanHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock

class ScanHistoryScreenViewModel(
    observeScanHistoryUseCase: ObserveScanHistoryUseCase,
    private val deleteScanHistoryItemUseCase: DeleteScanHistoryItemUseCase,
    private val clearScanHistoryUseCase: ClearScanHistoryUseCase,
) : ViewModel() {
    val viewState: StateFlow<ScanHistoryListViewState> = observeScanHistoryUseCase()
        .map { history ->
            ScanHistoryListViewState(
                historyItems = history.map { it.toUi(Clock.System.now().toEpochMilliseconds()) },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ScanHistoryListViewState(),
        )

    fun onDelete(id: Long) {
        viewModelScope.launch {
            deleteScanHistoryItemUseCase(id)
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clearScanHistoryUseCase()
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
                NavRoute.ScanDetails(
                    id = item.id
                )
            )
        }
    }
}
