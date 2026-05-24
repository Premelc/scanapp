package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.domelabs.scanapp.core.scan.CodeFormat
import com.domelabs.scanapp.core.scan.CodeKind
import com.domelabs.scanapp.core.scan.ScanCodePlatform
import com.domelabs.scanapp.core.scan.rememberCodeShareActions
import com.domelabs.scanapp.core.utils.rememberClipboardManager
import com.domelabs.scanapp.feature.scan.impl.domain.model.ScanHistoryItem
import com.domelabs.scanapp.uiComponent.components.LocalScreenMetrics
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.theme.ScanAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDetailsScreen(id: Long) {
    val viewModel: ScanDetailsViewModel = koinViewModel(key = id.toString()) {
        parametersOf(id)
    }

    val state by viewModel.viewState.collectAsStateWithLifecycle()

    when (val historyItem = state.historyItem) {
        null -> ScanDetailsLoading()
        else -> ScanDetailsContent(state.showShareSheet, historyItem, viewModel::onInteraction)
    }
}

@Composable
private fun ScanDetailsLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Loading scan details...",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ScanDetailsContent(
    showShareSheet: Boolean,
    historyItem: ScanHistoryItem,
    onInteraction: (ScanDetailsInteraction) -> Unit,
) {
    val screenMetrics = LocalScreenMetrics.current
    val scope = rememberCoroutineScope()
    val shareActions = rememberCodeShareActions()
    val copyToClipboard = rememberClipboardManager()
    var isPreparingShareImage by remember { mutableStateOf(false) }
    val codeByteArray = remember(historyItem.rawValue, historyItem.codeFormat) {
        generateDisplayPng(
            rawValue = historyItem.rawValue,
            codeFormat = historyItem.codeFormat,
            sizePx = screenMetrics.widthPx
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                    onInteraction(ScanDetailsInteraction.Back)
                },
            )
            NeoBrutalButton(
                text = "Share",
                style = NeoBrutalButtonStyle.Primary,
                onClick = { onInteraction(ScanDetailsInteraction.Share) },
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
            codeByteArray?.let {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    bitmap = it.decodeToImageBitmap(),
                    contentDescription = formatLabel(historyItem.codeFormat.name),
                    contentScale = ContentScale.FillWidth,
                )
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
                label = historyItem.codeKind.name,
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            InfoPill(
                label = formatLabel(historyItem.codeFormat.name),
                container = MaterialTheme.colorScheme.secondaryContainer,
                content = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            InfoPill(
                label = historyItem.source.name,
                container = MaterialTheme.colorScheme.tertiaryContainer,
                content = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        RawValueDetailRow(
            value = historyItem.rawValue,
            onCopy = { copyToClipboard(historyItem.rawValue) },
        )
        DetailRow("Scanned at", formatScannedAt(historyItem.timestampEpochMillis))

        if (showShareSheet) {
            ShareScanModal(
                onDismiss = {
                    if (!isPreparingShareImage) {
                        onInteraction(ScanDetailsInteraction.DismissShare)
                    }
                },
                kind = historyItem.codeKind.name,
                rawValue = historyItem.rawValue,
                isPreparingShareImage = isPreparingShareImage,
                onShareImage = {
                    if (isPreparingShareImage) return@ShareScanModal
                    isPreparingShareImage = true
                    // Close sheet first so bottom-sheet scrim is not included in the capture.
                    onInteraction(ScanDetailsInteraction.DismissShare)
                    scope.launch {
                        val fallbackBytes = withContext(Dispatchers.Default) {
                            generateDisplayPng(
                                rawValue = historyItem.rawValue,
                                codeFormat = historyItem.codeFormat,
                                sizePx = if (historyItem.codeKind == CodeKind.QR) 1200 else 1600,
                            )
                        }
                        if (fallbackBytes != null) {
                            shareActions.shareImage(
                                pngBytes = fallbackBytes,
                                fileName = "scan-${historyItem.codeFormat.name.lowercase()}.png",
                            )
                        }
                        isPreparingShareImage = false
                    }
                },
            )
        }
    }
}

@Composable
private fun RawValueDetailRow(
    value: String,
    onCopy: () -> Unit,
) {
    var expanded by remember(value) { mutableStateOf(false) }
    var hasOverflow by remember(value) { mutableStateOf(false) }

    NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Raw value",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
                RawValueCopyAction(onClick = onCopy)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if (!expanded) {
                        hasOverflow = textLayoutResult.hasVisualOverflow
                    }
                },
            )
            if (hasOverflow || expanded) {
                RawValueExpandAction(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                )
            }
        }
    }
}

@Composable
private fun RawValueCopyAction(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .semantics { role = Role.Button },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(ScanAppTheme.Icons.copy),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color.Unspecified,
        )
        Text(
            text = "Copy",
            style = MaterialTheme.typography.labelLarge.copy(
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@Composable
private fun RawValueExpandAction(
    expanded: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = if (expanded) "Show less" else "Show more",
        modifier = Modifier
            .clickable(onClick = onClick)
            .semantics { role = Role.Button },
        style = MaterialTheme.typography.labelLarge.copy(
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
        ),
    )
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

private fun formatLabel(codeFormat: String): String {
    return when (codeFormat) {
        "PDF_417" -> "PDF-417"
        "GS1_DATABAR" -> "GS1 DataBar"
        "GS1_DATABAR_EXPANDED" -> "GS1 DataBar Expanded"
        else -> codeFormat.replace('_', ' ')
    }
}

private fun formatScannedAt(epochMillis: Long): String {
    val dt = Instant
        .fromEpochMilliseconds(epochMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    fun Int.twoDigits() = toString().padStart(2, '0')

    return "${dt.year}-${dt.month.number.twoDigits()}-${dt.day.twoDigits()} " +
            "${dt.hour.twoDigits()}:${dt.minute.twoDigits()}:${dt.second.twoDigits()}"
}

private fun generateDisplayPng(
    rawValue: String,
    codeFormat: CodeFormat,
    sizePx: Int,
): ByteArray? {
    val direct = ScanCodePlatform.generatePng(
        rawValue = rawValue,
        codeFormat = codeFormat.name,
        sizePx = sizePx,
    )
    if (direct != null) return direct

    val isGs1DataBar = codeFormat == CodeFormat.GS1_DATABAR ||
            codeFormat == CodeFormat.GS1_DATABAR_EXPANDED
    if (!isGs1DataBar) return null

    // Current generator stack does not support writing GS1 DataBar directly.
    // Use CODE_128 as a display/share fallback to keep a visual representation.
    return ScanCodePlatform.generatePng(
        rawValue = rawValue,
        codeFormat = CodeFormat.CODE_128.name,
        sizePx = sizePx,
    )
}