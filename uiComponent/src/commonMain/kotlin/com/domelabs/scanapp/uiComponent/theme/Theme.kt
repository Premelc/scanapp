package com.domelabs.scanapp.uiComponent.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ScanAppColorScheme = darkColorScheme(
    // Primary colors - Cyan/Turquoise accent
    primary = BrightCyan,
    onPrimary = TextOnCyan,
    primaryContainer = NavyBlue,
    onPrimaryContainer = GlowingCyan,

    // Secondary colors - Turquoise/Green
    secondary = Turquoise,
    onSecondary = TextOnCyan,
    secondaryContainer = DarkBlueGray,
    onSecondaryContainer = SoftCyan,

    // Tertiary colors - Neon Green
    tertiary = NeonGreen,
    onTertiary = TextOnCyan,
    tertiaryContainer = MidnightBlue,
    onTertiaryContainer = NeonGreen,

    // Error colors
    error = ErrorRed,
    onError = TextPrimary,
    errorContainer = Color(0xFF3D1319),
    onErrorContainer = ErrorRed,

    // Background colors - Deep Navy
    background = DeepNavy,
    onBackground = TextPrimary,

    // Surface colors - Layered blues
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    surfaceTint = BrightCyan,

    // Outline colors
    outline = Color(0xFF3D4A5C),
    outlineVariant = Color(0xFF2A3544),

    // Other
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE8F0FF),
    inverseOnSurface = DeepNavy,
    inversePrimary = Color(0xFF006780),
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
