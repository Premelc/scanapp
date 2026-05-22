package com.domelabs.scanapp.core.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AppSnackbarHost(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    LaunchedEffect(hostState) {
        AppSnackbarDispatcher.events().collectLatest { event ->
            hostState.currentSnackbarData?.dismiss()
            val autoDismissJob = launch {
                delay(event.durationMillis)
                val visuals = hostState.currentSnackbarData?.visuals as? AppSnackbarVisuals
                if (visuals?.event === event) {
                    hostState.currentSnackbarData?.dismiss()
                }
            }

            val result = hostState.showSnackbar(AppSnackbarVisuals(event))
            autoDismissJob.cancel()

            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
        }
    }

    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
    ) { snackbarData ->
        AppSnackbarCard(snackbarData)
    }
}

private data class AppSnackbarVisuals(
    val event: AppSnackbarEvent,
) : SnackbarVisuals {
    override val actionLabel: String? = event.actionLabel
    override val withDismissAction: Boolean = event.dismissible
    override val duration: SnackbarDuration = SnackbarDuration.Indefinite
    override val message: String = event.title
}

@Composable
private fun AppSnackbarCard(snackbarData: SnackbarData) {
    val visuals = snackbarData.visuals as? AppSnackbarVisuals ?: run {
        Text(
            text = snackbarData.visuals.message,
            modifier = Modifier.padding(12.dp),
        )
        return
    }

    val event = visuals.event
    var dragOffsetX by remember(event) { mutableFloatStateOf(0f) }
    val dismissThresholdPx = with(LocalDensity.current) { 100.dp.toPx() }
    val badge = when (event.kind) {
        AppSnackbarKind.QrSuccess -> "QR"
        AppSnackbarKind.BarcodeSuccess -> "BAR"
        AppSnackbarKind.Error -> "ERR"
        AppSnackbarKind.Warning -> "WARN"
        AppSnackbarKind.Success -> "OK"
        AppSnackbarKind.Info -> "INFO"
    }
    val toneColor = when (event.kind) {
        AppSnackbarKind.QrSuccess -> MaterialTheme.colorScheme.primary
        AppSnackbarKind.BarcodeSuccess -> MaterialTheme.colorScheme.tertiary
        AppSnackbarKind.Success -> MaterialTheme.colorScheme.primary
        AppSnackbarKind.Warning -> MaterialTheme.colorScheme.tertiary
        AppSnackbarKind.Error -> MaterialTheme.colorScheme.error
        AppSnackbarKind.Info -> MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .graphicsLayer { translationX = dragOffsetX }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    dragOffsetX += delta
                },
                onDragStopped = {
                    if (abs(dragOffsetX) > dismissThresholdPx) {
                        snackbarData.dismiss()
                    } else {
                        dragOffsetX = 0f
                    }
                },
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Badge(badge = badge, toneColor = toneColor)
                Column {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = toneColor,
                    )
                    event.subtitle?.let { subtitle ->
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            event.actionLabel?.let { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { snackbarData.performAction() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

@Composable
private fun Badge(
    badge: String,
    toneColor: Color,
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(toneColor.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = badge,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = toneColor,
        )
    }
}
