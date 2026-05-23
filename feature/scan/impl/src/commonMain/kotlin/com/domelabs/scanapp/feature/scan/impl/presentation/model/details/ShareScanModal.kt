package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.core.scan.rememberCodeShareActions
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareScanModal(
    kind: String,
    rawValue: String,
    isPreparingShareImage: Boolean,
    onShareImage: () -> Unit,
    onDismiss:()->Unit,
){
    val shareActions = rememberCodeShareActions()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Share code",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            ShareActionRow(
                title = "Share as image",
                description = "Share rendered ${kind.lowercase()} image",
                enabled = !isPreparingShareImage,
                onClick = {
                    onShareImage()
                },
            )
            ShareActionRow(
                title = "Share as text",
                description = "Share the raw code content",
                enabled = true,
                onClick = {
                    shareActions.shareText(rawValue)
                    onDismiss()
                },
            )
        }
    }
}

@Composable
private fun ShareActionRow(
    title: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    NeoBrutalCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick),
        showShadow = false,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.5f
                ),
            )
        }
    }
}