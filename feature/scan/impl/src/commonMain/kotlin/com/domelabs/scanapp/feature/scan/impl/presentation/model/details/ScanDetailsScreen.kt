package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.core.navigation.NavRoute
import com.domelabs.scanapp.core.navigation.NavigationDispatcher
import com.domelabs.scanapp.core.scan.GeneratedCodeMatrix
import com.domelabs.scanapp.core.scan.rememberCodeShareActions
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailsScreen(
    route: NavRoute.ScanDetails,
    viewModel: ScanDetailsScreenViewModel = koinInject(),
) {
    val shareActions = rememberCodeShareActions()
    val scope = rememberCoroutineScope()
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    var showShareSheet by remember { mutableStateOf(false) }
    var isPreparingShareImage by remember { mutableStateOf(false) }

    LaunchedEffect(route.rawValue, route.codeFormat) {
        viewModel.load(route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NeoBrutalButton(
                text = "Back",
                style = NeoBrutalButtonStyle.Secondary,
                onClick = {
                    scope.launch { NavigationDispatcher.back() }
                },
            )
            NeoBrutalButton(
                text = "Share",
                style = NeoBrutalButtonStyle.Primary,
                onClick = { showShareSheet = true },
                leadingIcon = {
                    Icon(
                        painter = painterResource(ScanAppTheme.Icons.share),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                },
            )
        }

        NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
            when (val matrixState = state.matrixState) {
                CodePreviewState.Loading -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is CodePreviewState.Ready -> {
                    CodeMatrixPreview(
                        matrix = matrixState.matrix,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                CodePreviewState.Unavailable -> {
                    Text(
                        text = "Preview unavailable for ${formatLabel(route.codeFormat)}.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Text(
            text = "Scanned item details",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            InfoPill(
                label = route.codeKind,
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            InfoPill(
                label = formatLabel(route.codeFormat),
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            InfoPill(
                label = route.source,
                container = MaterialTheme.colorScheme.tertiaryContainer,
                content = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        DetailRow("Raw value", route.rawValue, singleLine = false)
        DetailRow("Scanned at", state.scannedAtLabel)
    }

    if (showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
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
                    description = "Share rendered ${route.codeKind.lowercase()} image",
                    enabled = state.matrixState is CodePreviewState.Ready && !isPreparingShareImage,
                    onClick = {
                        scope.launch {
                            isPreparingShareImage = true
                            try {
                                val pngBytes = viewModel.generateImageForShare(route)
                                if (pngBytes != null) {
                                    shareActions.shareImage(
                                        pngBytes = pngBytes,
                                        fileName = "scan-${route.codeFormat.lowercase()}.png",
                                    )
                                    showShareSheet = false
                                }
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } finally {
                                isPreparingShareImage = false
                            }
                        }
                    },
                )
                ShareActionRow(
                    title = "Share as text",
                    description = "Share the raw code content",
                    enabled = true,
                    onClick = {
                        shareActions.shareText(route.rawValue)
                        showShareSheet = false
                    },
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, singleLine: Boolean = true) {
    NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun InfoPill(
    label: String,
    container: Color,
    content: Color,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = content,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(container)
            .border(1.dp, content.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@Composable
private fun CodeMatrixPreview(
    matrix: GeneratedCodeMatrix,
    modifier: Modifier = Modifier,
) {
    val ratio = matrix.width.toFloat() / matrix.height.toFloat()
    Canvas(
        modifier = modifier
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color.Black.copy(alpha = 0.25f), RoundedCornerShape(8.dp)),
    ) {
        val cellWidth = size.width / matrix.width.toFloat()
        val cellHeight = size.height / matrix.height.toFloat()
        for (y in 0 until matrix.height) {
            for (x in 0 until matrix.width) {
                if (matrix.bits[y * matrix.width + x]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(
                            x * cellWidth,
                            y * cellHeight,
                        ),
                        size = Size(cellWidth, cellHeight),
                    )
                }
            }
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
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }
    }
}

private fun formatLabel(codeFormat: String): String {
    return when (codeFormat) {
        "PDF_417" -> "PDF-417"
        else -> codeFormat.replace('_', ' ')
    }
}

sealed interface CodePreviewState {
    data object Loading : CodePreviewState
    data object Unavailable : CodePreviewState
    data class Ready(val matrix: GeneratedCodeMatrix) : CodePreviewState
}
