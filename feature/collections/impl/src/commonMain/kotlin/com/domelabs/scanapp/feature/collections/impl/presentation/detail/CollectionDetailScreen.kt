package com.domelabs.scanapp.feature.collections.impl.presentation.detail

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.parseHexColor
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBadgeButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalTextField
import com.domelabs.scanapp.uiComponent.components.ScreenTopBar
import com.domelabs.scanapp.uiComponent.components.shadow.LazyShadowColumn
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CollectionDetailScreen(id: Long) {
    val viewModel: CollectionDetailViewModel = koinViewModel(key = id.toString()) {
        parametersOf(id)
    }
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Header(
            state = state,
            onInteraction = viewModel::onInteraction,
        )

        SearchBar(
            query = state.searchQuery,
            onQueryChange = { viewModel.onInteraction(CollectionDetailInteraction.UpdateSearchQuery(it)) },
            onClear = { viewModel.onInteraction(CollectionDetailInteraction.ClearSearchQuery) },
        )

        Body(
            state = state,
            onInteraction = viewModel::onInteraction,
        )
    }

    state.editForm?.let { form ->
        EditCollectionSheet(
            form = form,
            availableColors = state.availableColors,
            onInteraction = viewModel::onInteraction,
        )
    }
}

@Composable
private fun Header(
    state: CollectionDetailViewState,
    onInteraction: (CollectionDetailInteraction) -> Unit,
) {
    val collection = state.collection
    ScreenTopBar(
        onBack = { onInteraction(CollectionDetailInteraction.Back) },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val color = collection?.colorHex
                    ?.let { parseHexColor(it) }
                    ?: parseHexColor("#D9D9D9")
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(1.dp, NeoBlack, CircleShape),
                )
                Text(
                    text = collection?.name ?: "Loading…",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        actions = {
            if (collection != null && !collection.isSystemManaged) {
                Box {
                    NeoBrutalIconBadgeButton(
                        icon = ScanAppTheme.Icons.more,
                        contentDescription = "Collection options",
                        onClick = { onInteraction(CollectionDetailInteraction.OpenOverflowMenu) },
                    )
                    DropdownMenu(
                        expanded = state.showOverflowMenu,
                        onDismissRequest = { onInteraction(CollectionDetailInteraction.DismissOverflowMenu) },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { onInteraction(CollectionDetailInteraction.OpenEditSheet) },
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { onInteraction(CollectionDetailInteraction.OpenDeleteConfirmation) },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NeoBrutalTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Search items",
            modifier = Modifier.weight(1f),
        )
        if (query.isNotEmpty()) {
            NeoBrutalButton(
                text = "Clear",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = onClear,
            )
        }
    }
}

@Composable
private fun Body(
    state: CollectionDetailViewState,
    onInteraction: (CollectionDetailInteraction) -> Unit,
) {
    when {
        state.items.isEmpty() && !state.isSearching -> {
            EmptyMessage("This collection is empty.")
        }

        state.items.isEmpty() && state.isSearching -> {
            EmptyMessage("No matches for \"${state.searchQuery}\".")
        }

        else -> {
            LazyShadowColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                items(state.items, key = { it.id }) { item ->
                    ItemRow(
                        item = item,
                        onClick = { onInteraction(CollectionDetailInteraction.OpenItem(item.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage(text: String) {
    NeoBrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ItemRow(
    item: ScannedItem,
    onClick: () -> Unit,
) {
    NeoBrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        showShadow = false,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.customName ?: item.rawValue,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${item.codeKind} • ${item.codeFormat} • ${item.source.name}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditCollectionSheet(
    form: EditCollectionForm,
    availableColors: List<String>,
    onInteraction: (CollectionDetailInteraction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = { onInteraction(CollectionDetailInteraction.DismissEditSheet) },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Edit collection",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            NeoBrutalTextField(
                value = form.name,
                onValueChange = { onInteraction(CollectionDetailInteraction.UpdateEditName(it)) },
                placeholder = "Collection name",
            )
            Text(
                text = "Color",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                availableColors.forEach { color ->
                    ColorSwatch(
                        colorHex = color,
                        selected = color.equals(form.colorHex, ignoreCase = true),
                        onClick = { onInteraction(CollectionDetailInteraction.SelectEditColor(color)) },
                    )
                }
            }
            form.error?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NeoBrutalButton(
                    text = "Cancel",
                    style = NeoBrutalButtonStyle.Secondary,
                    onClick = { onInteraction(CollectionDetailInteraction.DismissEditSheet) },
                )
                NeoBrutalButton(
                    text = if (form.isSubmitting) "Saving…" else "Save",
                    style = NeoBrutalButtonStyle.Primary,
                    enabled = !form.isSubmitting && form.name.trim().isNotEmpty(),
                    onClick = { onInteraction(CollectionDetailInteraction.SubmitEdit) },
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    colorHex: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = parseHexColor(colorHex)
    val borderColor = if (selected) NeoBlack else NeoGrayPlaceholder
    Box(
        modifier = Modifier
            .size(if (selected) 36.dp else 30.dp)
            .clip(CircleShape)
            .background(color)
            .border(width = if (selected) 3.dp else 1.dp, color = borderColor, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(NeoWhite)
                    .border(1.dp, NeoBlack, CircleShape),
            )
        }
    }
}
