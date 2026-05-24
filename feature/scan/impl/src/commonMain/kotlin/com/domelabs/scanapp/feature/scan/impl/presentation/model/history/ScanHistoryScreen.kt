package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBadgeButton
import com.domelabs.scanapp.uiComponent.components.shadow.LazyShadowColumn
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.koin.compose.koinInject

@Composable
fun ScanHistoryScreen(
    viewModel: ScanHistoryScreenViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
            modifier = Modifier.padding(horizontal = 16.dp),
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
            LazyShadowColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(state.historyItems, key = { it.id }) { item ->
                    NeoBrutalCard(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .padding(end = 8.dp)
                                    .clickable { viewModel.openDetails(item) },
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = item.rawValue,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
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
                            NeoBrutalIconBadgeButton(
                                icon = ScanAppTheme.Icons.trash,
                                contentDescription = "Delete scan",
                                onClick = { viewModel.onDelete(item.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
