package com.domelabs.scanapp.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.core.notification.ActiveAppConfirmation
import com.domelabs.scanapp.core.notification.AppConfirmationDispatcher
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppConfirmationHost() {
    val active by AppConfirmationDispatcher.active.collectAsStateWithLifecycle()
    active?.let { confirmation ->
        key(confirmation.id) {
            ConfirmationBottomSheet(confirmation = confirmation)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationBottomSheet(
    confirmation: ActiveAppConfirmation,
) {
    val request = confirmation.request
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { AppConfirmationDispatcher.dismiss(invokeCancel = true) }
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = request.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = request.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NeoBrutalButton(
                    text = request.confirmLabel,
                    style = NeoBrutalButtonStyle.Primary,
                    onClick = {
                        scope.launch { AppConfirmationDispatcher.confirmPrimary() }
                    },
                )
                request.secondaryConfirmLabel?.let { label ->
                    NeoBrutalButton(
                        text = label,
                        style = NeoBrutalButtonStyle.Secondary,
                        onClick = {
                            scope.launch { AppConfirmationDispatcher.confirmSecondary() }
                        },
                    )
                }
                NeoBrutalButton(
                    text = request.cancelLabel,
                    style = NeoBrutalButtonStyle.Secondary,
                    onClick = {
                        scope.launch { AppConfirmationDispatcher.dismiss(invokeCancel = true) }
                    },
                )
            }
        }
    }
}
