package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ClearScanHistoryUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.DeleteScanHistoryItemUseCase
import com.domelabs.scanapp.feature.scan.impl.domain.usecase.ObserveScanHistoryUseCase
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Clock
import org.koin.compose.koinInject

data class ScanHistoryListViewState(
    val historyItems: List<ScanHistoryItemUi> = emptyList(),
)

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
                    rawValue = item.rawValue,
                    codeKind = item.codeKind,
                    codeFormat = item.codeFormat,
                    source = item.source,
                    scannedAtEpochMillis = item.scannedAtEpochMillis,
                )
            )
        }
    }
}

@Composable
fun ScanHistoryScreen(
    viewModel: ScanHistoryScreenViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeoBrutalButton(
                text = "Back",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = viewModel::onBack,
            )
            NeoBrutalButton(
                text = "Clear",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = viewModel::onClear,
                enabled = state.historyItems.isNotEmpty(),
            )
        }

        Text(
            text = "History",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )

        if (state.historyItems.isEmpty()) {
            NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No scan history yet.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.historyItems, key = { it.id }) { item ->
                    NeoBrutalCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openDetails(item) },
                        showShadow = false,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = item.rawValue,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                Text(
                                    text = item.relativeTime,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            NeoBrutalButton(
                                text = "Del",
                                style = NeoBrutalButtonStyle.Secondary,
                                onClick = { viewModel.onDelete(item.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
