package com.domelabs.scanapp.feature.collections.impl.presentation

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
import com.domelabs.scanapp.feature.collections.impl.presentation.create.CreateCollectionSheet
import com.domelabs.scanapp.feature.collections.impl.presentation.list.CollectionRowUi
import com.domelabs.scanapp.feature.collections.impl.presentation.list.CollectionsListInteraction
import com.domelabs.scanapp.feature.collections.impl.presentation.list.CollectionsListViewModel
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.parseHexColor
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBadgeButton
import com.domelabs.scanapp.uiComponent.components.ScreenTopBar
import com.domelabs.scanapp.uiComponent.components.shadow.LazyShadowColumn
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectionsScreen(
    viewModel: CollectionsListViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTopBar(
            title = "Collections",
            onBack = { viewModel.onInteraction(CollectionsListInteraction.Back) },
            actions = {
                NeoBrutalIconBadgeButton(
                    icon = ScanAppTheme.Icons.addFolder,
                    contentDescription = "New collection",
                    onClick = { viewModel.onInteraction(CollectionsListInteraction.OpenCreateSheet) },
                    backgroundColor = NeoBlack,
                    iconTint = NeoWhite,
                )
            },
        )

        if (state.rows.isEmpty()) {
            NeoBrutalCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    text = "No collections yet.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyShadowColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                items(state.rows, key = { it.id }) { row ->
                    CollectionRow(
                        row = row,
                        onClick = {
                            viewModel.onInteraction(CollectionsListInteraction.OpenCollection(row.id))
                        },
                    )
                }
            }
        }
    }

    if (state.showCreateSheet) {
        CreateCollectionSheet(
            onCreated = {
                viewModel.onInteraction(CollectionsListInteraction.DismissCreateSheet)
            },
            onDismiss = {
                viewModel.onInteraction(CollectionsListInteraction.DismissCreateSheet)
            },
        )
    }
}

@Composable
private fun CollectionRow(
    row: CollectionRowUi,
    onClick: () -> Unit,
) {
    NeoBrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        showShadow = false,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(parseHexColor(row.colorHex))
                    .border(1.dp, NeoBlack, CircleShape),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = row.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (row.isSystemManaged) {
                        Text(
                            text = "system",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text(
                    text = row.itemCountLabel,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = row.lastUpdatedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
