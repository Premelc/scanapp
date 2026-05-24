package com.domelabs.scanapp.notification

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
import androidx.compose.material3.Icon
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
import com.domelabs.scanapp.core.notification.AppSnackbarDispatcher
import com.domelabs.scanapp.core.notification.AppSnackbarEvent
import com.domelabs.scanapp.core.notification.AppSnackbarKind
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelBlue
import com.domelabs.scanapp.uiComponent.theme.PastelGreen
import com.domelabs.scanapp.uiComponent.theme.PastelMint
import com.domelabs.scanapp.uiComponent.theme.PastelOrange
import com.domelabs.scanapp.uiComponent.theme.PastelSalmon
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import com.domelabs.scanapp.uiComponent.theme.ScanAppTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

private val SnackbarTitleColor = NeoBlack
private val SnackbarSubtitleColor = NeoBlack.copy(alpha = 0.72f)

private data class SnackbarVisualStyle(
    val badgeColor: Color,
    val icon: DrawableResource,
    val contentDescription: String,
)

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
            color = SnackbarTitleColor,
        )
        return
    }

    val event = visuals.event
    val style = snackbarVisualStyle(event.kind)
    var dragOffsetX by remember(event) { mutableFloatStateOf(0f) }
    val dismissThresholdPx = with(LocalDensity.current) { 100.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .graphicsLayer { translationX = dragOffsetX }
            .neoBrutalStyle(
                backgroundColor = NeoWhite,
                cornerRadius = NeoBrutalism.CornerRadius,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadius))
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
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SnackbarBadge(
                icon = style.icon,
                contentDescription = style.contentDescription,
                badgeColor = style.badgeColor,
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = event.title,
                    style = ScanAppTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SnackbarTitleColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                event.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = ScanAppTypography.bodyMedium,
                        color = SnackbarSubtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            event.actionLabel?.let { label ->
                SnackbarActionButton(
                    label = label,
                    onClick = { snackbarData.performAction() },
                )
            }
        }
    }
}

@Composable
private fun SnackbarActionButton(
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .neoBrutalStyle(
                backgroundColor = PastelMint,
                cornerRadius = NeoBrutalism.CornerRadiusSmall,
                shadowOffsetX = NeoBrutalism.ShadowOffsetPressedX,
                shadowOffsetY = NeoBrutalism.ShadowOffsetPressedY,
            )
            .clip(RoundedCornerShape(NeoBrutalism.CornerRadiusSmall))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = ScanAppTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SnackbarTitleColor,
        )
    }
}

private fun snackbarVisualStyle(kind: AppSnackbarKind): SnackbarVisualStyle = when (kind) {
    AppSnackbarKind.QrSuccess -> SnackbarVisualStyle(
        badgeColor = PastelMint,
        icon = ScanAppTheme.Icons.qr,
        contentDescription = "QR code",
    )
    AppSnackbarKind.BarcodeSuccess -> SnackbarVisualStyle(
        badgeColor = PastelOrange,
        icon = ScanAppTheme.Icons.barcode,
        contentDescription = "Barcode",
    )
    AppSnackbarKind.Success -> SnackbarVisualStyle(
        badgeColor = PastelGreen,
        icon = ScanAppTheme.Icons.checkCircle,
        contentDescription = "Success",
    )
    AppSnackbarKind.Warning -> SnackbarVisualStyle(
        badgeColor = PastelOrange,
        icon = ScanAppTheme.Icons.warning,
        contentDescription = "Warning",
    )
    AppSnackbarKind.Error -> SnackbarVisualStyle(
        badgeColor = PastelSalmon,
        icon = ScanAppTheme.Icons.closeCircleOutlined,
        contentDescription = "Error",
    )
    AppSnackbarKind.Info -> SnackbarVisualStyle(
        badgeColor = PastelBlue,
        icon = ScanAppTheme.Icons.info,
        contentDescription = "Information",
    )
}

@Composable
private fun SnackbarBadge(
    icon: DrawableResource,
    contentDescription: String,
    badgeColor: Color,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .neoBrutalStyle(
                backgroundColor = badgeColor,
                cornerRadius = NeoBrutalism.CornerRadiusSmall,
                shadowOffsetX = NeoBrutalism.ShadowOffsetPressedX,
                shadowOffsetY = NeoBrutalism.ShadowOffsetPressedY,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = NeoBlack,
            modifier = Modifier.size(22.dp),
        )
    }
}
