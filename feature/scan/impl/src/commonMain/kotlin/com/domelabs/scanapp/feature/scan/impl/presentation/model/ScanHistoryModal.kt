package com.domelabs.scanapp.feature.scan.impl.presentation.model

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import kotlinx.coroutines.flow.drop

@Composable
internal fun ScanHistoryDrawerLayout(
    isHistoryDrawerOpen: Boolean,
    historyItems: List<ScanHistoryItemUi>,
    close: () -> Unit,
    deleteItem: (Long) -> Unit,
    clear: () -> Unit,
    openDetails: (ScanHistoryItemUi) -> Unit,
    content: @Composable () -> Unit,
){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(isHistoryDrawerOpen) {
        if (isHistoryDrawerOpen && drawerState.currentValue != DrawerValue.Open) {
            drawerState.open()
        } else if (!isHistoryDrawerOpen && drawerState.currentValue != DrawerValue.Closed) {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState, isHistoryDrawerOpen) {
        snapshotFlow { drawerState.currentValue }
            .drop(1)
            .collect { drawerValue ->
                if (drawerValue == DrawerValue.Closed && isHistoryDrawerOpen) {
                    close()
                }
            }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.fillMaxWidth(0.82f),
                    ) {
                        HistoryDrawerSheetContent(
                            historyItems = historyItems,
                            onClose = close,
                            onDeleteItem = deleteItem,
                            onClearAll = clear,
                            onOpenDetails = openDetails,
                        )
                    }
                }
            },
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    }
}

@Composable
private fun HistoryDrawerSheetContent(
    historyItems: List<ScanHistoryItemUi>,
    onClose: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    onClearAll: () -> Unit,
    onOpenDetails: (ScanHistoryItemUi) -> Unit,
) {
    NeoBrutalCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeoBrutalButton(
                    text = "Clear",
                    style = NeoBrutalButtonStyle.Secondary,
                    onClick = onClearAll,
                    enabled = historyItems.isNotEmpty(),
                )
                NeoBrutalButton(
                    text = "Close",
                    style = NeoBrutalButtonStyle.Primary,
                    onClick = onClose,
                )
            }
        }

        if (historyItems.isEmpty()) {
            Text(
                text = "No scan history yet.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(historyItems, key = { it.id }) { item ->
                    NeoBrutalCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { onOpenDetails(item) }),
                        showShadow = false,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.rawValue,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                                Text(
                                    text = item.relativeTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 2.dp),
                                )
                            }
                            NeoBrutalButton(
                                text = "Del",
                                style = NeoBrutalButtonStyle.Secondary,
                                onClick = { onDeleteItem(item.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}