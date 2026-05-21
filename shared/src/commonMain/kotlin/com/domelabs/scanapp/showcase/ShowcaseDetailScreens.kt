@file:OptIn(ExperimentalLayoutApi::class)

package com.domelabs.scanapp.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalButtonStyle
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCategoryCard
import com.domelabs.scanapp.uiComponent.components.NeoBrutalCheckbox
import com.domelabs.scanapp.uiComponent.components.NeoBrutalChip
import com.domelabs.scanapp.uiComponent.components.NeoBrutalDashedBox
import com.domelabs.scanapp.uiComponent.components.NeoBrutalFloatingActionButton
import com.domelabs.scanapp.uiComponent.components.NeoBrutalIconBox
import com.domelabs.scanapp.uiComponent.components.NeoBrutalSearchBar
import com.domelabs.scanapp.uiComponent.components.NeoBrutalTextField
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalShadow
import com.domelabs.scanapp.uiComponent.modifier.neoBrutalStyle
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.PastelBlue
import com.domelabs.scanapp.uiComponent.theme.PastelGreen
import com.domelabs.scanapp.uiComponent.theme.PastelMint
import com.domelabs.scanapp.uiComponent.theme.PastelOrange
import com.domelabs.scanapp.uiComponent.theme.PastelSalmon
import com.domelabs.scanapp.uiComponent.theme.ScanAppPastels

@Composable
fun ButtonsShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var styleIndex by remember { mutableIntStateOf(0) }
    val styles = NeoBrutalButtonStyle.entries

    ShowcaseDetailScreen(
        title = "Buttons",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
            RowOfChips(
                labels = styles.map { it.name },
                selectedIndex = styleIndex,
                onSelect = { styleIndex = it },
            )
        },
        preview = {
            NeoBrutalButton(
                text = "Search",
                onClick = {},
                style = styles[styleIndex],
                enabled = enabled,
                pastelColor = PastelOrange,
            )
            NeoBrutalButton(
                text = "Heatmap",
                onClick = {},
                style = NeoBrutalButtonStyle.Secondary,
                enabled = enabled,
            )
            NeoBrutalButton(
                text = "Radius",
                onClick = {},
                style = NeoBrutalButtonStyle.Pastel,
                enabled = enabled,
                pastelColor = PastelMint,
            )
        },
    )
}

@Composable
fun TextFieldsShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }

    ShowcaseDetailScreen(
        title = "Text fields",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
        },
        preview = {
            NeoBrutalTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = "Enter location...",
                enabled = enabled,
            )
            NeoBrutalTextField(
                value = "Pre-filled value",
                onValueChange = {},
                enabled = enabled,
            )
        },
    )
}

@Composable
fun SearchBarShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }

    ShowcaseDetailScreen(
        title = "Search bar",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
        },
        preview = {
            NeoBrutalSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {},
                enabled = enabled,
            )
        },
    )
}

@Composable
fun ChipsShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var heatmapSelected by remember { mutableStateOf(true) }
    var radiusSelected by remember { mutableStateOf(false) }

    ShowcaseDetailScreen(
        title = "Chips",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
        },
        preview = {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NeoBrutalChip(
                    label = "Heatmap",
                    selected = heatmapSelected,
                    onClick = {
                        heatmapSelected = true
                        radiusSelected = false
                    },
                    icon = Icons.Default.Place,
                    enabled = enabled,
                )
                NeoBrutalChip(
                    label = "Radius",
                    selected = radiusSelected,
                    onClick = {
                        radiusSelected = true
                        heatmapSelected = false
                    },
                    icon = Icons.Default.Star,
                    enabled = enabled,
                )
            }
        },
    )
}

@Composable
fun CheckboxesShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var checked by remember { mutableStateOf(true) }

    ShowcaseDetailScreen(
        title = "Checkboxes",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
            ShowcaseToggleRow("Checked", checked) { checked = it }
        },
        preview = {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                NeoBrutalCheckbox(checked = false, onCheckedChange = {}, enabled = enabled)
                NeoBrutalCheckbox(checked = true, onCheckedChange = {}, enabled = enabled)
                NeoBrutalCheckbox(checked = checked, onCheckedChange = { checked = it }, enabled = enabled)
            }
        },
    )
}

@Composable
fun CardsShowcaseScreen(onBack: () -> Unit) {
    var showShadow by remember { mutableStateOf(true) }

    ShowcaseDetailScreen(
        title = "Cards",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Shadow", showShadow) { showShadow = it }
        },
        preview = {
            NeoBrutalCard(showShadow = showShadow) {
                Text("Livability Score", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Card content with neobrutal border and hard shadow.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
    )
}

@Composable
fun CategoryCardsShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var cityMarkets by remember { mutableStateOf(true) }
    var schools by remember { mutableStateOf(false) }

    ShowcaseDetailScreen(
        title = "Category cards",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
        },
        preview = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NeoBrutalCategoryCard(
                    title = "City Markets",
                    count = 31,
                    icon = Icons.Default.Home,
                    iconBackgroundColor = PastelOrange,
                    checked = cityMarkets,
                    onCheckedChange = { cityMarkets = it },
                    onClick = {},
                    enabled = enabled,
                )
                NeoBrutalCategoryCard(
                    title = "Elementary Schools",
                    count = 12,
                    icon = Icons.Default.Person,
                    iconBackgroundColor = PastelGreen,
                    checked = schools,
                    onCheckedChange = { schools = it },
                    onClick = {},
                    enabled = enabled,
                )
                NeoBrutalCategoryCard(
                    title = "Health Centers",
                    count = 8,
                    icon = Icons.Default.LocationOn,
                    iconBackgroundColor = PastelSalmon,
                    checked = false,
                    onCheckedChange = {},
                    onClick = {},
                    enabled = enabled,
                )
            }
        },
    )
}

@Composable
fun FabShowcaseScreen(onBack: () -> Unit) {
    var enabled by remember { mutableStateOf(true) }
    var colorIndex by remember { mutableIntStateOf(0) }
    val colors = ScanAppPastels.all

    ShowcaseDetailScreen(
        title = "FAB",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Enabled", enabled) { enabled = it }
            RowOfChips(
                labels = colors.indices.map { "Color ${it + 1}" },
                selectedIndex = colorIndex,
                onSelect = { colorIndex = it },
            )
        },
        preview = {
            NeoBrutalFloatingActionButton(
                onClick = {},
                icon = Icons.Default.Add,
                backgroundColor = colors[colorIndex],
                enabled = enabled,
            )
        },
    )
}

@Composable
fun IconBoxesShowcaseScreen(onBack: () -> Unit) {
    val icons = listOf(
        Icons.Default.Home to PastelOrange,
        Icons.Default.Person to PastelGreen,
        Icons.Default.LocationOn to PastelSalmon,
        Icons.Default.Place to PastelBlue,
    )

    ShowcaseDetailScreen(
        title = "Icon boxes",
        onBack = onBack,
        preview = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                icons.forEach { (icon, color) ->
                    NeoBrutalIconBox(icon = icon, backgroundColor = color)
                }
            }
        },
    )
}

@Composable
fun DashedBoxShowcaseScreen(onBack: () -> Unit) {
    ShowcaseDetailScreen(
        title = "Dashed box",
        onBack = onBack,
        preview = {
            NeoBrutalDashedBox(label = "Drop livability data here")
            NeoBrutalDashedBox(
                label = "No score yet",
                height = 80.dp,
            )
        },
    )
}

@Composable
fun ShadowsShowcaseScreen(onBack: () -> Unit) {
    var offsetLarge by remember { mutableStateOf(true) }
    var customColor by remember { mutableStateOf(false) }

    ShowcaseDetailScreen(
        title = "Shadow modifier",
        onBack = onBack,
        controls = {
            ShowcaseToggleRow("Large offset (4dp)", offsetLarge) { offsetLarge = it }
            ShowcaseToggleRow("Pastel shadow", customColor) { customColor = it }
        },
        preview = {
            val offsetX = if (offsetLarge) NeoBrutalism.ShadowOffsetX else NeoBrutalism.ShadowOffsetPressedX
            val offsetY = if (offsetLarge) NeoBrutalism.ShadowOffsetY else NeoBrutalism.ShadowOffsetPressedY
            val shadowColor = if (customColor) PastelBlue else Color.Black

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .neoBrutalShadow(
                        offsetX = offsetX,
                        offsetY = offsetY,
                        color = shadowColor,
                    )
                    .neoBrutalStyle(backgroundColor = NeoWhite, showShadow = false),
            ) {
                Text(
                    "Modifier.neoBrutalShadow()",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Text(
                "Tip: apply neoBrutalShadow before background/border, or use neoBrutalStyle() for the full treatment.",
                style = MaterialTheme.typography.bodySmall,
            )
        },
    )
}

@Composable
private fun RowOfChips(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.forEachIndexed { index, label ->
            NeoBrutalChip(
                label = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
            )
        }
    }
}
