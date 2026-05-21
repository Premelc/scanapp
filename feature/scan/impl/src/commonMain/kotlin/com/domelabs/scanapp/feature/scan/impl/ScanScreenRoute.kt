package com.domelabs.scanapp.feature.scan.impl

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.core.scan.CodeScanner
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalBorder
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onInteraction(ScanInteraction.RequestCameraPermission)
    }

    ScanScreenContent(
        state = state,
        onInteraction = viewModel::onInteraction,
    )
}

@Composable
private fun ScanScreenContent(
    state: ScanViewState,
    onInteraction: (ScanInteraction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.permission == ScanPermissionState.Granted) {
            CodeScanner(
                modifier = Modifier.fillMaxSize(),
                enabled = state.isScannerActive,
                flashEnabled = state.flashEnabled,
                onDetected = { onInteraction(ScanInteraction.CodeDetected(it)) },
                onError = { onInteraction(ScanInteraction.ScanFailed(it)) },
                cooldownMillis = 2_000L,
            )
            ScanOverlay(
                flashEnabled = state.flashEnabled,
                onToggleFlashlight = { onInteraction(ScanInteraction.ToggleFlashlight) },
                onGallery = { onInteraction(ScanInteraction.OpenGalleryPicker) },
            )
            state.error?.let {
                ErrorOverlay(onRetry = { onInteraction(ScanInteraction.RetryAfterError) })
            }
        } else {
            PermissionOverlay(
                permissionState = state.permission,
                onRequestPermission = { onInteraction(ScanInteraction.RequestCameraPermission) },
            )
        }
    }
}

@Composable
private fun ScanOverlay(
    flashEnabled: Boolean,
    onToggleFlashlight: () -> Unit,
    onGallery: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            NeoBrutalButton(
                text = if (flashEnabled) "Flash on" else "Flash off",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = onToggleFlashlight,
            )
        }

        AnimatedScanWindow(
            modifier = Modifier.align(Alignment.Center),
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
        ) {
            NeoBrutalButton(
                text = "Open gallery",
                onClick = onGallery,
                style = NeoBrutalButtonStyle.Secondary,
            )
        }
    }
}

@Composable
private fun AnimatedScanWindow(
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val scanBoxSize = maxWidth.coerceAtMost(maxHeight) * 0.65f
        val lineHeight = 4.dp
        val transition = rememberInfiniteTransition(label = "scan-line")
        val progress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "scan-line-progress",
        )
        val lineTravelPx = with(LocalDensity.current) { (scanBoxSize - lineHeight).toPx() }

        Box(
            modifier = Modifier.size(scanBoxSize).neoBrutalBorder()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lineHeight)
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (lineTravelPx * progress).roundToInt(),
                        )
                    }
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun PermissionOverlay(
    permissionState: ScanPermissionState,
    onRequestPermission: () -> Unit,
) {
    val denied = permissionState == ScanPermissionState.Denied
    val message = if (denied) {
        "Camera access denied. Open settings to continue scanning."
    } else {
        "Camera access is required to scan QR and bar codes."
    }
    val cta = if (denied) "Open settings" else "Grant camera access"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        NeoBrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Camera Permission",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(text = message, style = MaterialTheme.typography.bodyLarge)
                NeoBrutalButton(
                    text = cta,
                    onClick = onRequestPermission,
                )
            }
        }
    }
}

@Composable
private fun ErrorOverlay(
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        NeoBrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Camera Error",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "Camera is unavailable right now. Please retry.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                NeoBrutalButton(
                    text = "Retry",
                    style = NeoBrutalButtonStyle.Primary,
                    onClick = onRetry,
                )
            }
        }
    }
}
