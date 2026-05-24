package com.domelabs.designShowcase.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.theme.BrightCyan
import com.domelabs.scanapp.uiComponent.theme.DeepNavy
import com.domelabs.scanapp.uiComponent.theme.GlowingCyan
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoGrayLight
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder
import com.domelabs.scanapp.uiComponent.theme.NeoWhite
import com.domelabs.scanapp.uiComponent.theme.ScanAppPastels
import com.domelabs.scanapp.uiComponent.theme.SuccessGreen
import com.domelabs.scanapp.uiComponent.theme.SurfaceDark
import com.domelabs.scanapp.uiComponent.theme.WarningAmber

private data class ColorSwatch(
    val name: String,
    val color: Color,
)

@Composable
fun ColorsShowcaseScreen(onBack: () -> Unit) {
    ShowcaseDetailScreen(
        title = "Colors",
        onBack = onBack,
        preview = {
            ColorSection(
                title = "Neobrutal base",
                swatches = listOf(
                    ColorSwatch("NeoBlack", NeoBlack),
                    ColorSwatch("NeoWhite", NeoWhite),
                    ColorSwatch("NeoGrayLight", NeoGrayLight),
                    ColorSwatch("NeoGrayPlaceholder", NeoGrayPlaceholder),
                ),
            )
            ColorSection(
                title = "Category pastels",
                swatches = ScanAppPastels.all.mapIndexed { index, color ->
                    ColorSwatch("Pastel ${index + 1}", color)
                },
            )
            ColorSection(
                title = "Scan overlay accents",
                swatches = listOf(
                    ColorSwatch("BrightCyan", BrightCyan),
                    ColorSwatch("GlowingCyan", GlowingCyan),
                    ColorSwatch("DeepNavy", DeepNavy),
                    ColorSwatch("SurfaceDark", SurfaceDark),
                ),
            )
            ColorSection(
                title = "Status",
                swatches = listOf(
                    ColorSwatch("SuccessGreen", SuccessGreen),
                    ColorSwatch("WarningAmber", WarningAmber),
                    ColorSwatch("Error", MaterialTheme.colorScheme.error),
                ),
            )
            ColorSection(
                title = "Material theme",
                swatches = listOf(
                    ColorSwatch("Primary", MaterialTheme.colorScheme.primary),
                    ColorSwatch("Secondary", MaterialTheme.colorScheme.secondary),
                    ColorSwatch("Tertiary", MaterialTheme.colorScheme.tertiary),
                    ColorSwatch("Surface", MaterialTheme.colorScheme.surface),
                    ColorSwatch("Surface high", MaterialTheme.colorScheme.surfaceContainerHigh),
                ),
            )
        },
    )
}

@Composable
private fun ColorSection(
    title: String,
    swatches: List<ColorSwatch>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false,
        ) {
            items(swatches, key = { it.name }) { swatch ->
                ColorSwatchTile(swatch)
            }
        }
    }
}

@Composable
private fun ColorSwatchTile(swatch: ColorSwatch) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(swatch.color)
                .border(2.dp, NeoBlack, RoundedCornerShape(8.dp)),
        )
        Text(
            text = swatch.name,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
