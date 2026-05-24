package com.domelabs.scanapp.feature.collections.impl.presentation.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.feature.collections.impl.domain.model.CollectionSummary
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalTextField
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionPickerSheet(
    currentCollectionId: Long?,
    onSelected: (collectionId: Long) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CollectionPickerViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(currentCollectionId) {
        viewModel.setCurrentCollectionId(currentCollectionId)
    }

    LaunchedEffect(viewModel) {
        viewModel.pickedCollectionEvents.collect { id -> onSelected(id) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        if (state.createMode) {
            CreateCollectionForm(
                state = state,
                onInteraction = viewModel::onInteraction,
            )
        } else {
            PickerList(
                state = state,
                onInteraction = viewModel::onInteraction,
            )
        }
    }
}

@Composable
private fun PickerList(
    state: CollectionPickerViewState,
    onInteraction: (CollectionPickerInteraction) -> Unit,
) {
    val sorted = remember(state.collections) {
        sortCollections(state.collections)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Move to collection",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp, max = 360.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            items(sorted, key = { it.collection.id }) { summary ->
                PickerRow(
                    summary = summary,
                    selected = summary.collection.id == state.currentCollectionId,
                    onClick = {
                        onInteraction(
                            CollectionPickerInteraction.PickCollection(summary.collection.id)
                        )
                    },
                )
            }
        }
        NeoBrutalButton(
            text = "+ New collection",
            style = NeoBrutalButtonStyle.Secondary,
            onClick = { onInteraction(CollectionPickerInteraction.EnterCreateMode) },
        )
    }
}

@Composable
private fun PickerRow(
    summary: CollectionSummary,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = parseHexColor(summary.collection.colorHex)
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
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, NeoBlack, CircleShape),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = summary.collection.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = itemCountLabel(summary.itemCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (selected) {
                Text(
                    text = "Current",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun CreateCollectionForm(
    state: CollectionPickerViewState,
    onInteraction: (CollectionPickerInteraction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "New collection",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        NeoBrutalTextField(
            value = state.newName,
            onValueChange = { onInteraction(CollectionPickerInteraction.UpdateNewName(it)) },
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
            state.availableColors.forEach { colorHex ->
                ColorSwatch(
                    colorHex = colorHex,
                    selected = colorHex.equals(state.newColorHex, ignoreCase = true),
                    onClick = { onInteraction(CollectionPickerInteraction.SelectNewColor(colorHex)) },
                )
            }
        }
        state.createError?.let { error ->
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
                onClick = { onInteraction(CollectionPickerInteraction.ExitCreateMode) },
            )
            NeoBrutalButton(
                text = if (state.isSubmittingNewCollection) "Creating…" else "Create",
                style = NeoBrutalButtonStyle.Primary,
                enabled = state.canSubmitNewCollection,
                onClick = { onInteraction(CollectionPickerInteraction.SubmitNewCollection) },
            )
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

private fun itemCountLabel(count: Int): String = when {
    count == 0 -> "Empty"
    count == 1 -> "1 item"
    count > 99 -> "99+ items"
    else -> "$count items"
}

private fun sortCollections(items: List<CollectionSummary>): List<CollectionSummary> {
    val unspecified = items.firstOrNull { it.collection.isSystemManaged }
    val rest = items
        .filter { !it.collection.isSystemManaged }
        .sortedBy { it.collection.name.lowercase() }
    return if (unspecified != null) listOf(unspecified) + rest else rest
}
