package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.onFocusChanged
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
import com.domelabs.scanapp.feature.collections.impl.domain.model.Collection
import com.domelabs.scanapp.feature.collections.impl.domain.model.ScannedItem
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.CollectionPickerSheet
import com.domelabs.scanapp.feature.collections.impl.presentation.picker.parseHexColor
import com.domelabs.scanapp.uiComponent.components.LocalScreenMetrics
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBadgeButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalTextField
import com.domelabs.scanapp.uiComponent.components.ScreenTopBar
import com.domelabs.scanapp.uiComponent.components.shadow.ScrollableShadowColumn
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
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
fun ItemDetailsScreen(id: Long) {
    val viewModel: ItemDetailsViewModel = koinViewModel(key = id.toString()) {
        parametersOf(id)
    }

    val state by viewModel.viewState.collectAsStateWithLifecycle()

    when (val item = state.item) {
        null -> ItemDetailsLoading(onBack = { viewModel.onInteraction(ItemDetailsInteraction.Back) })
        else -> ItemDetailsContent(
            state = state,
            item = item,
            onInteraction = viewModel::onInteraction,
        )
    }
}

@Composable
private fun ItemDetailsLoading(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        ScreenTopBar(
            title = "Item details",
            onBack = onBack,
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Loading item details…",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ItemDetailsContent(
    state: ItemDetailsViewState,
    item: ScannedItem,
    onInteraction: (ItemDetailsInteraction) -> Unit,
) {
    val screenMetrics = LocalScreenMetrics.current
    val scope = rememberCoroutineScope()
    val shareActions = rememberCodeShareActions()
    val copyToClipboard = rememberClipboardManager()
    var isPreparingShareImage by remember { mutableStateOf(false) }
    val codeByteArray = remember(item.rawValue, item.codeFormat) {
        generateDisplayPng(
            rawValue = item.rawValue,
            codeFormat = item.codeFormat,
            sizePx = screenMetrics.widthPx,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenTopBar(
            title = "Item details",
            onBack = { onInteraction(ItemDetailsInteraction.Back) },
            actions = {
                NeoBrutalIconBadgeButton(
                    icon = ScanAppTheme.Icons.trash,
                    contentDescription = "Delete item",
                    onClick = { onInteraction(ItemDetailsInteraction.Delete) },
                )
                NeoBrutalIconBadgeButton(
                    icon = ScanAppTheme.Icons.share,
                    contentDescription = "Share item",
                    onClick = { onInteraction(ItemDetailsInteraction.Share) },
                    backgroundColor = NeoBlack,
                    iconTint = NeoWhite,
                )
            },
        )
        ScrollableShadowColumn(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NeoBrutalCard(modifier = Modifier.fillMaxWidth()) {
                codeByteArray?.let {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = it.decodeToImageBitmap(),
                        contentDescription = formatLabel(item.codeFormat.name),
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }

            ItemMetadataSection(
                collection = state.collection,
                customNameDraft = state.customNameDraft,
                customNameLimit = state.customNameLimit,
                onCollectionClick = { onInteraction(ItemDetailsInteraction.OpenCollectionPicker) },
                onCustomNameChange = { onInteraction(ItemDetailsInteraction.UpdateCustomNameDraft(it)) },
                onCustomNameCommit = { onInteraction(ItemDetailsInteraction.CommitCustomName) },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoPill(
                    label = item.codeKind.name,
                    container = MaterialTheme.colorScheme.primaryContainer,
                    content = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                InfoPill(
                    label = formatLabel(item.codeFormat.name),
                    container = MaterialTheme.colorScheme.secondaryContainer,
                    content = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                InfoPill(
                    label = item.source.name,
                    container = MaterialTheme.colorScheme.tertiaryContainer,
                    content = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }

            RawValueDetailRow(
                value = item.rawValue,
                onCopy = { copyToClipboard(item.rawValue) },
            )
            DetailRow("Scanned at", formatScannedAt(item.timestampEpochMillis))
        }
        if (state.showShareSheet) {
            ShareScanModal(
                onDismiss = {
                    if (!isPreparingShareImage) {
                        onInteraction(ItemDetailsInteraction.DismissShare)
                    }
                },
                kind = item.codeKind.name,
                rawValue = item.rawValue,
                isPreparingShareImage = isPreparingShareImage,
                onShareImage = {
                    if (isPreparingShareImage) return@ShareScanModal
                    isPreparingShareImage = true
                    onInteraction(ItemDetailsInteraction.DismissShare)
                    scope.launch {
                        val fallbackBytes = withContext(Dispatchers.Default) {
                            generateDisplayPng(
                                rawValue = item.rawValue,
                                codeFormat = item.codeFormat,
                                sizePx = if (item.codeKind == CodeKind.QR) 1200 else 1600,
                            )
                        }
                        if (fallbackBytes != null) {
                            shareActions.shareImage(
                                pngBytes = fallbackBytes,
                                fileName = "scan-${item.codeFormat.name.lowercase()}.png",
                            )
                        }
                        isPreparingShareImage = false
                    }
                },
            )
        }
        if (state.showCollectionPicker) {
            CollectionPickerSheet(
                currentCollectionId = item.collectionId,
                onSelected = { collectionId ->
                    onInteraction(ItemDetailsInteraction.MoveToCollection(collectionId))
                },
                onDismiss = {
                    onInteraction(ItemDetailsInteraction.DismissCollectionPicker)
                },
            )
        }
    }
}

@Composable
private fun ItemMetadataSection(
    collection: Collection?,
    customNameDraft: String,
    customNameLimit: Int,
    onCollectionClick: () -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomNameCommit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CollectionChipRow(
            collection = collection,
            onClick = onCollectionClick,
        )
        CustomNameEditor(
            draft = customNameDraft,
            limit = customNameLimit,
            onDraftChange = onCustomNameChange,
            onCommit = onCustomNameCommit,
        )
    }
}

@Composable
private fun CollectionChipRow(
    collection: Collection?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Collection",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable(onClick = onClick)
                .border(1.dp, NeoBlack, RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        collection?.colorHex
                            ?.let { parseHexColor(it) }
                            ?: Color.Gray
                    )
                    .border(1.dp, NeoBlack, CircleShape),
            )
            Text(
                text = collection?.name ?: "Unspecified",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Change",
                style = MaterialTheme.typography.labelSmall.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                ),
            )
        }
    }
}

@Composable
private fun CustomNameEditor(
    draft: String,
    limit: Int,
    onDraftChange: (String) -> Unit,
    onCommit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Custom name",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${draft.length}/$limit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        NeoBrutalTextField(
            value = draft,
            onValueChange = onDraftChange,
            placeholder = "Optional",
            modifier = Modifier.onFocusChanged { focusState ->
                if (!focusState.isFocused) onCommit()
            },
        )
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

    return ScanCodePlatform.generatePng(
        rawValue = rawValue,
        codeFormat = CodeFormat.CODE_128.name,
        sizePx = sizePx,
    )
}
