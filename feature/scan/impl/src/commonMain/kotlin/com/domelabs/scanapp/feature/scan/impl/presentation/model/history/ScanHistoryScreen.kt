package com.domelabs.scanapp.feature.scan.impl.presentation.model.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.parseHexColor
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBadgeButton
import com.domelabs.scanapp.uiComponent.components.ScreenTopBar
import com.domelabs.scanapp.uiComponent.components.shadow.LazyShadowColumn
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
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
        ScreenTopBar(
            title = "Recent",
            onBack = viewModel::onBack,
        )

        if (state.historyItems.isEmpty()) {
            NeoBrutalCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    text = "No scans yet.",
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
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = item.displayName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                CollectionPillRow(
                                    name = item.collectionName,
                                    colorHex = item.collectionColorHex,
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

@Composable
private fun CollectionPillRow(
    name: String,
    colorHex: String,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, NeoBlack.copy(alpha = 0.45f), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(parseHexColor(colorHex))
                .border(1.dp, NeoBlack, CircleShape),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
