package com.domelabs.designShowcase.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
import com.domelabs.scanapp.notification.AppSnackbarHost
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import kotlinx.coroutines.launch

@Composable
fun SnackbarsShowcaseScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var withSubtitle by remember { mutableStateOf(true) }
    var withAction by remember { mutableStateOf(true) }

    Scaffold(
        snackbarHost = { AppSnackbarHost() },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NeoBrutalButton(
                text = "Back",
                onClick = onBack,
                style = NeoBrutalButtonStyle.Secondary,
            )
            Text(
                text = "SNACKBARS",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "Tap a kind to show a snackbar. Swipe horizontally to dismiss.",
                style = MaterialTheme.typography.bodyMedium,
            )

            NeoBrutalCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Controls",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    ShowcaseToggleRow("Subtitle", withSubtitle) { withSubtitle = it }
                    ShowcaseToggleRow("Action button", withAction) { withAction = it }
                }
            }

            NeoBrutalCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Kinds",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    AppSnackbarKind.entries.forEach { kind ->
                        NeoBrutalButton(
                            text = kind.name,
                            onClick = {
                                scope.launch {
                                    AppSnackbarDispatcher.dispatch(
                                        sampleSnackbarEvent(
                                            kind = kind,
                                            withSubtitle = withSubtitle,
                                            withAction = withAction,
                                        ),
                                    )
                                }
                            },
                            style = NeoBrutalButtonStyle.Secondary,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

private fun sampleSnackbarEvent(
    kind: AppSnackbarKind,
    withSubtitle: Boolean,
    withAction: Boolean,
): AppSnackbarEvent {
    val (title, subtitle) = when (kind) {
        AppSnackbarKind.QrSuccess -> "QR code scanned" to "https://domelabs.dev"
        AppSnackbarKind.BarcodeSuccess -> "Barcode scanned" to "5901234123457"
        AppSnackbarKind.Success -> "Saved to history" to "Tap Details to open the entry"
        AppSnackbarKind.Warning -> "No code found" to "Try better lighting or move closer"
        AppSnackbarKind.Error -> "Scan failed" to "Camera is unavailable right now"
        AppSnackbarKind.Info -> "Tip" to "Hold steady for faster detection"
    }

    return AppSnackbarEvent(
        title = title,
        subtitle = subtitle.takeIf { withSubtitle },
        actionLabel = "Details".takeIf { withAction },
        kind = kind,
        onAction = {},
    )
}
