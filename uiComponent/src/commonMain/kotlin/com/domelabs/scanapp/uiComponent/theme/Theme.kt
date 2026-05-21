package com.domelabs.scanapp.uiComponent.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ScanAppColorScheme = lightColorScheme(
    primary = NeoBlack,
    onPrimary = NeoWhite,
    primaryContainer = PastelBlue,
    onPrimaryContainer = NeoBlack,

    secondary = PastelMint,
    onSecondary = NeoBlack,
    secondaryContainer = PastelGreen,
    onSecondaryContainer = NeoBlack,

    tertiary = PastelOrange,
    onTertiary = NeoBlack,
    tertiaryContainer = PastelSalmon,
    onTertiaryContainer = NeoBlack,

    error = Color(0xFFFF6B6B),
    onError = NeoWhite,
    errorContainer = PastelSalmon,
    onErrorContainer = NeoBlack,

    background = NeoWhite,
    onBackground = NeoBlack,

    surface = NeoWhite,
    onSurface = NeoBlack,
    surfaceVariant = NeoGrayLight,
    onSurfaceVariant = NeoBlack,
    surfaceTint = NeoBlack,

    outline = NeoBlack,
    outlineVariant = NeoGrayPlaceholder,

    scrim = NeoBlack,
    inverseSurface = NeoBlack,
    inverseOnSurface = NeoWhite,
    inversePrimary = PastelBlue,
)

@Composable
fun ProvideScanAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ScanAppColorScheme,
        typography = ScanAppTypography,
        content = content
    )
}
