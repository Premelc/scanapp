package com.domelabs.scanapp.feature.scan.impl.presentation.model.scan

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.uiComponent.theme.BrightCyan
import com.domelabs.scanapp.core.media.MediaItem
import com.domelabs.scanapp.core.media.compress
import com.domelabs.scanapp.core.media.rememberMediaPicker
import com.domelabs.scanapp.core.scan.CameraZoomState
import com.domelabs.scanapp.core.scan.CodeScanner
import com.domelabs.scanapp.core.scan.rememberCameraZoomState
import com.domelabs.scanapp.core.scan.rememberGalleryCodeScanner
import com.domelabs.scanapp.feature.scan.impl.presentation.model.ScanMenuDrawerLayout
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

private object ScanOverlayColors {
    val fabBackground = Color(0xFF121826).copy(alpha = 0.62f)
    val fabBorder = Color.White.copy(alpha = 0.28f)
    val fabIcon = Color.White.copy(alpha = 0.95f)
    val flashActive = Color(0xFFFFB74D)
    val flashActiveIcon = Color(0xFF2A1800)
    val viewfinderCorner = Color.White.copy(alpha = 0.92f)
    val scrim = Color.Black.copy(alpha = 0.2f)
}

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = koinInject(),
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val zoomState = rememberCameraZoomState()
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
        zoomState = zoomState,
        onInteraction = viewModel::onInteraction,
        openGalleryPicker = openGalleryPicker,
    )
}

@Composable
private fun ScanScreenContent(
    state: ScanViewState,
    zoomState: CameraZoomState,
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
                    zoomState = zoomState,
                )
                ScanViewfinderOverlay(modifier = Modifier.fillMaxSize())
            } else {
                PermissionOverlay(
                    permissionState = state.permission,
                    onRequestPermission = { onInteraction(ScanInteraction.RequestCameraPermission) },
                )
            }

            ScanOverlayControls(
                permissionGranted = state.permission == ScanPermissionState.Granted,
                flashEnabled = state.flashEnabled,
                zoomState = if (state.permission == ScanPermissionState.Granted) zoomState else null,
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
    zoomState: CameraZoomState?,
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
                        tint = ScanOverlayColors.fabIcon,
                    )
                },
                onClick = onSettings,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (permissionGranted && zoomState?.isAvailable == true) {
                ScanZoomSlider(
                    zoomState = zoomState,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (permissionGranted) {
                    ScanControlButton(
                        icon = {
                            Icon(
                                painter = painterResource(if (flashEnabled) ScanAppTheme.Icons.flashOn else ScanAppTheme.Icons.flashOff),
                                contentDescription = "Flashlight",
                                tint = if (flashEnabled) ScanOverlayColors.flashActiveIcon else ScanOverlayColors.fabIcon,
                            )
                        },
                        onClick = onToggleFlashlight,
                        highlighted = flashEnabled,
                        highlightColor = ScanOverlayColors.flashActive,
                    )
                } else {
                    SpacerFabPlaceholder()
                }

                ScanControlButton(
                    icon = {
                        Icon(
                            painter = painterResource(ScanAppTheme.Icons.gallery),
                            contentDescription = "Gallery",
                            tint = ScanOverlayColors.fabIcon,
                        )
                    },
                    onClick = onGallery,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanZoomSlider(
    zoomState: CameraZoomState,
    modifier: Modifier = Modifier,
) {
    val zoomStep = remember(zoomState.minZoomRatio, zoomState.maxZoomRatio) {
        ((zoomState.maxZoomRatio - zoomState.minZoomRatio) / 12f).coerceAtLeast(0.05f)
    }

    fun adjustZoom(delta: Float) {
        zoomState.setZoomRatio(zoomState.zoomRatio + delta)
        zoomState.onZoomAdjustFinished()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable { adjustZoom(-zoomStep) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(ScanAppTheme.Icons.zoomOut),
                contentDescription = "Zoom out",
                tint = ScanOverlayColors.fabIcon,
                modifier = Modifier.size(20.dp),
            )
        }

        Slider(
            value = zoomState.zoomRatio,
            onValueChange = { zoomState.setZoomRatio(it) },
            onValueChangeFinished = { zoomState.onZoomAdjustFinished() },
            valueRange = zoomState.minZoomRatio..zoomState.maxZoomRatio,
            modifier = Modifier
                .weight(1f)
                .height(28.dp),
            colors = SliderDefaults.colors(
                thumbColor = BrightCyan,
                activeTrackColor = BrightCyan,
                inactiveTrackColor = Color.White.copy(alpha = 0.22f),
            ),
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clickable { adjustZoom(zoomStep) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(ScanAppTheme.Icons.zoomIn),
                contentDescription = "Zoom in",
                tint = ScanOverlayColors.fabIcon,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ScanControlButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    highlightColor: Color = ScanOverlayColors.fabBackground,
) {
    val background = if (highlighted) highlightColor.copy(alpha = 0.92f) else ScanOverlayColors.fabBackground
    val border = if (highlighted) highlightColor.copy(alpha = 0.95f) else ScanOverlayColors.fabBorder

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, border, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

@Composable
private fun NeoBrutalTextFab(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScanControlButton(
        icon = icon,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun SpacerFabPlaceholder() {
    Box(modifier = Modifier.size(52.dp))
}

@Composable
private fun ScanViewfinderOverlay(
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val frameSize = minOf(maxWidth, maxHeight) * 0.68f
        val frameSizePx = with(density) { frameSize.toPx() }
        val cornerRadiusPx = with(density) { 22.dp.toPx() }
        val cornerArmPx = with(density) { (frameSize * 0.14f).toPx() }
        val strokePx = with(density) { 3.dp.toPx() }

        val transition = rememberInfiniteTransition(label = "scan-viewfinder")
        val cornerPulse by transition.animateFloat(
            initialValue = 0.72f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "corner-pulse",
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameLeft = (size.width - frameSizePx) / 2f
            val frameTop = (size.height - frameSizePx) / 2f
            val hole = RoundRect(
                rect = Rect(
                    offset = Offset(frameLeft, frameTop),
                    size = Size(frameSizePx, frameSizePx),
                ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
            )

            val scrimPath = Path().apply {
                addRect(Rect(Offset.Zero, size))
                addRoundRect(hole)
                fillType = PathFillType.EvenOdd
            }
            drawPath(scrimPath, ScanOverlayColors.scrim)

            val cornerColor = ScanOverlayColors.viewfinderCorner.copy(alpha = cornerPulse)
            drawViewfinderCorner(
                origin = Offset(frameLeft, frameTop),
                armLength = cornerArmPx,
                cornerRadius = cornerRadiusPx,
                stroke = strokePx,
                color = cornerColor,
                quadrant = ViewfinderQuadrant.TopLeft,
            )
            drawViewfinderCorner(
                origin = Offset(frameLeft + frameSizePx, frameTop),
                armLength = cornerArmPx,
                cornerRadius = cornerRadiusPx,
                stroke = strokePx,
                color = cornerColor,
                quadrant = ViewfinderQuadrant.TopRight,
            )
            drawViewfinderCorner(
                origin = Offset(frameLeft, frameTop + frameSizePx),
                armLength = cornerArmPx,
                cornerRadius = cornerRadiusPx,
                stroke = strokePx,
                color = cornerColor,
                quadrant = ViewfinderQuadrant.BottomLeft,
            )
            drawViewfinderCorner(
                origin = Offset(frameLeft + frameSizePx, frameTop + frameSizePx),
                armLength = cornerArmPx,
                cornerRadius = cornerRadiusPx,
                stroke = strokePx,
                color = cornerColor,
                quadrant = ViewfinderQuadrant.BottomRight,
            )
        }
    }
}

private const val ScanBeamCycleMillis = 4000

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawScanBeam(
    frameLeft: Float,
    frameWidth: Float,
    beamY: Float,
    accent: Color,
    beamHeightPx: Float,
    glowSpreadPx: Float,
) {
    val horizontalInset = frameWidth * 0.03f
    val beamLeft = frameLeft + horizontalInset
    val beamWidth = frameWidth - horizontalInset * 2f
    val sideExtension = glowSpreadPx * 2.5f
    val drawLeft = beamLeft - sideExtension
    val drawWidth = beamWidth + sideExtension * 2f

    fun horizontalGlowStops(peakAlpha: Float) = arrayOf(
        0f to Color.Transparent,
        0.08f to accent.copy(alpha = peakAlpha * 0.25f),
        0.22f to accent.copy(alpha = peakAlpha * 0.65f),
        0.5f to accent.copy(alpha = peakAlpha),
        0.78f to accent.copy(alpha = peakAlpha * 0.65f),
        0.92f to accent.copy(alpha = peakAlpha * 0.25f),
        1f to Color.Transparent,
    )

    drawOval(
        brush = Brush.horizontalGradient(
            colorStops = horizontalGlowStops(0.2f),
            startX = drawLeft,
            endX = drawLeft + drawWidth,
        ),
        topLeft = Offset(drawLeft, beamY - glowSpreadPx),
        size = Size(drawWidth, glowSpreadPx * 2.4f),
    )

    drawOval(
        brush = Brush.horizontalGradient(
            colorStops = horizontalGlowStops(0.5f),
            startX = drawLeft,
            endX = drawLeft + drawWidth,
        ),
        topLeft = Offset(drawLeft, beamY - glowSpreadPx * 0.55f),
        size = Size(drawWidth, glowSpreadPx * 1.1f),
    )

    drawOval(
        brush = Brush.horizontalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.06f to accent.copy(alpha = 0.45f),
                0.2f to accent.copy(alpha = 0.82f),
                0.5f to Color.White.copy(alpha = 0.98f),
                0.8f to accent.copy(alpha = 0.82f),
                0.94f to accent.copy(alpha = 0.45f),
                1f to Color.Transparent,
            ),
            startX = drawLeft,
            endX = drawLeft + drawWidth,
        ),
        topLeft = Offset(drawLeft, beamY - beamHeightPx / 2f),
        size = Size(drawWidth, beamHeightPx.coerceAtLeast(glowSpreadPx * 0.35f)),
    )
}

private enum class ViewfinderQuadrant {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight,
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawViewfinderCorner(
    origin: Offset,
    armLength: Float,
    cornerRadius: Float,
    stroke: Float,
    color: Color,
    quadrant: ViewfinderQuadrant,
) {
    val path = Path()
    when (quadrant) {
        ViewfinderQuadrant.TopLeft -> {
            path.moveTo(origin.x, origin.y + armLength)
            path.lineTo(origin.x, origin.y + cornerRadius)
            path.quadraticTo(origin.x, origin.y, origin.x + cornerRadius, origin.y)
            path.lineTo(origin.x + armLength, origin.y)
        }

        ViewfinderQuadrant.TopRight -> {
            path.moveTo(origin.x - armLength, origin.y)
            path.lineTo(origin.x - cornerRadius, origin.y)
            path.quadraticTo(origin.x, origin.y, origin.x, origin.y + cornerRadius)
            path.lineTo(origin.x, origin.y + armLength)
        }

        ViewfinderQuadrant.BottomLeft -> {
            path.moveTo(origin.x, origin.y - armLength)
            path.lineTo(origin.x, origin.y - cornerRadius)
            path.quadraticTo(origin.x, origin.y, origin.x + cornerRadius, origin.y)
            path.lineTo(origin.x + armLength, origin.y)
        }

        ViewfinderQuadrant.BottomRight -> {
            path.moveTo(origin.x - armLength, origin.y)
            path.lineTo(origin.x - cornerRadius, origin.y)
            path.quadraticTo(origin.x, origin.y, origin.x, origin.y - cornerRadius)
            path.lineTo(origin.x, origin.y - armLength)
        }
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
    )
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
