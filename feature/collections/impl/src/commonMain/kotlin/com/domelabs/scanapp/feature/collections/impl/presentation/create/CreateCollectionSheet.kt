package com.domelabs.scanapp.feature.collections.impl.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.parseHexColor
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalTextField
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionSheet(
    onCreated: (collectionId: Long) -> Unit,
    onDismiss: () -> Unit,
    viewModel: CreateCollectionViewModel = koinViewModel(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(viewModel) {
        viewModel.createdEvents.collect { id -> onCreated(id) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                value = state.name,
                onValueChange = { viewModel.onInteraction(CreateCollectionInteraction.UpdateName(it)) },
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
                state.availableColors.forEach { color ->
                    ColorSwatch(
                        colorHex = color,
                        selected = color.equals(state.colorHex, ignoreCase = true),
                        onClick = {
                            viewModel.onInteraction(CreateCollectionInteraction.SelectColor(color))
                        },
                    )
                }
            }
            state.error?.let { error ->
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
                    onClick = onDismiss,
                )
                NeoBrutalButton(
                    text = if (state.isSubmitting) "Creating…" else "Create",
                    style = NeoBrutalButtonStyle.Primary,
                    enabled = state.canSubmit,
                    onClick = { viewModel.onInteraction(CreateCollectionInteraction.Submit) },
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
