package com.domelabs.scanapp.feature.scan.impl.presentation.model.scan

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.core.media.MediaItem
import com.domelabs.scanapp.core.media.compress
import com.domelabs.scanapp.core.media.rememberMediaPicker
import com.domelabs.scanapp.core.scan.CodeScanner
import com.domelabs.scanapp.core.scan.rememberGalleryCodeScanner
import com.domelabs.scanapp.feature.scan.impl.presentation.model.ScanMenuDrawerLayout
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalBorder
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val galleryCodeScanner = rememberGalleryCodeScanner()
    val openGalleryPicker = rememberMediaPicker { mediaItems ->
        val image = mediaItems.firstOrNull { it is MediaItem.Image } as? MediaItem.Image ?: return@rememberMediaPicker
        scope.launch {
            val preparedImage = image.compress()
            val scanned = galleryCodeScanner.scanCodeFromImageUri(preparedImage.uri)
            if (scanned == null) {
                viewModel.onInteraction(ScanInteraction.GalleryCodeNotFound)
            } else {
                viewModel.onInteraction(ScanInteraction.GalleryCodeDetected(scanned))
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onInteraction(ScanInteraction.RequestCameraPermission)
    }

    ScanScreenContent(
        state = state,
        onInteraction = viewModel::onInteraction,
        openGalleryPicker = openGalleryPicker,
    )
}

@Composable
private fun ScanScreenContent(
    state: ScanViewState,
    onInteraction: (ScanInteraction) -> Unit,
    openGalleryPicker: () -> Unit,
) {
    ScanMenuDrawerLayout(
        isMenuDrawerOpen = state.isMenuDrawerOpen,
        close = { onInteraction(ScanInteraction.CloseMenuDrawer) },
        navigateHistory = { onInteraction(ScanInteraction.NavigateToHistory) },
        navigateSettings = { onInteraction(ScanInteraction.NavigateToSettings) },
        navigateCollections = { onInteraction(ScanInteraction.NavigateToCollections) },
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.permission == ScanPermissionState.Granted) {
                CodeScanner(
                    modifier = Modifier.fillMaxSize(),
                    flashEnabled = state.flashEnabled,
                    onDetected = { onInteraction(ScanInteraction.CodeDetected(it)) },
                    onError = { onInteraction(ScanInteraction.ScanFailed(it)) },
                    cooldownMillis = 2_000L,
                )
                AnimatedScanWindow(modifier = Modifier.align(Alignment.Center))
            } else {
                PermissionOverlay(
                    permissionState = state.permission,
                    onRequestPermission = { onInteraction(ScanInteraction.RequestCameraPermission) },
                )
            }

            ScanOverlayControls(
                permissionGranted = state.permission == ScanPermissionState.Granted,
                flashEnabled = state.flashEnabled,
                onToggleFlashlight = { onInteraction(ScanInteraction.ToggleFlashlight) },
                onGallery = openGalleryPicker,
                onHistory = { onInteraction(ScanInteraction.NavigateToHistory) },
                onSettings = { onInteraction(ScanInteraction.OpenMenuDrawer) },
            )

            state.error?.let {
                ErrorOverlay(onRetry = { onInteraction(ScanInteraction.RetryAfterError) })
            }
        }
    }
}

@Composable
private fun ScanOverlayControls(
    permissionGranted: Boolean,
    flashEnabled: Boolean,
    onToggleFlashlight: () -> Unit,
    onGallery: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Placeholder for the scan strategy drop down button coming in the future
            Spacer(modifier = Modifier.weight(1f))
            NeoBrutalTextFab(
                icon = {
                    Icon(
                        painter = painterResource(ScanAppTheme.Icons.menu),
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                },
                onClick = onSettings,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (permissionGranted) {
                NeoBrutalTextFab(
                    icon = {
                        Icon(
                            painter = painterResource(if (flashEnabled) ScanAppTheme.Icons.flashOn else ScanAppTheme.Icons.flashOff),
                            contentDescription = "Flashlight",
                            tint = if (flashEnabled) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            },
                        )
                    },
                    onClick = onToggleFlashlight,
                    backgroundColor = if (flashEnabled) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
                    },
                )
            } else {
                SpacerFabPlaceholder()
            }

            NeoBrutalTextFab(
                icon = {
                    Icon(
                        painter = painterResource(ScanAppTheme.Icons.gallery),
                        contentDescription = "Gallery",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                },
                onClick = onGallery,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
private fun NeoBrutalTextFab(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    backgroundColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .neoBrutalStyle(
                backgroundColor = backgroundColor,
                cornerRadius = 28.dp,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
private fun SpacerFabPlaceholder() {
    Box(modifier = Modifier.size(56.dp))
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
