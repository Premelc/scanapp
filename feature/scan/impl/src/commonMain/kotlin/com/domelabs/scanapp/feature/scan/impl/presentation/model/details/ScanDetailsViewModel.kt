package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.feature.scan.impl.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class ScanDetailsViewModel(
    val id: Long,
    val scanHistoryRepository: ScanHistoryRepository,
) : ViewModel() {
    private val showShareSheet = MutableStateFlow(false)
    val viewState = combine(
        showShareSheet,
        scanHistoryRepository.historyItemFlow(id)
    ) { showShareSheet, historyItem ->
        ScanDetailsViewState(
            showShareSheet = showShareSheet,
            historyItem = historyItem
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5.seconds),
        ScanDetailsViewState()
    )

    fun onInteraction(interaction: ScanDetailsInteraction) {
        when (interaction) {
            is ScanDetailsInteraction.Back -> viewModelScope.launch {
                NavigationDispatcher.back()
            }

            is ScanDetailsInteraction.Delete -> viewModelScope.launch {
                scanHistoryRepository.deleteHistoryItem(id)
            }

            ScanDetailsInteraction.Share -> showShareSheet.update { true }

            ScanDetailsInteraction.DismissShare -> showShareSheet.update { false }
        }
    }


}