package com.domelabs.scanapp.uiComponent.theme

import androidx.compose.ui.graphics.Color

// Neobrutalism — core palette
val NeoBlack = Color(0xFF000000)
val NeoWhite = Color(0xFFFFFFFF)
val NeoGrayPlaceholder = Color(0xFF9E9E9E)
val NeoGrayLight = Color(0xFFF5F5F5)

// Neobrutalism — category pastels (from reference UI)
val PastelOrange = Color(0xFFFFCC80)
val PastelGreen = Color(0xFFB8E6A3)
val PastelSalmon = Color(0xFFFFB4A2)
val PastelOlive = Color(0xFFC5D4A0)
val PastelTan = Color(0xFFE6D5B8)
val PastelBlue = Color(0xFFA8D4FF)
val PastelMint = Color(0xFFB2DFDB)

object ScanAppPastels {
    val orange = PastelOrange
    val green = PastelGreen
    val salmon = PastelSalmon
    val olive = PastelOlive
    val tan = PastelTan
    val blue = PastelBlue
    val mint = PastelMint

    val all = listOf(orange, green, salmon, olive, tan, blue, mint)
}

// Legacy brand colors (kept for icons / future dark mode)
// Primary Colors - Deep Navy Blues (inspired by logo)
val DeepNavy = Color(0xFF0F1F3F)
val NavyBlue = Color(0xFF1B2B4B)
val DarkNavy = Color(0xFF0A1628)

// Accent Colors - Bright Cyan/Turquoise (inspired by logo)
val BrightCyan = Color(0xFF00D9FF)
val Turquoise = Color(0xFF00FFCC)
val NeonGreen = Color(0xFF00FF88)
val GlowingCyan = Color(0xFF5DFFFF)

// Secondary Colors - Muted versions for backgrounds
val DarkBlueGray = Color(0xFF1A2332)
val MidnightBlue = Color(0xFF0D1B2A)
val SoftCyan = Color(0xFF4DD4E8)

// Surface Colors
val SurfaceDark = Color(0xFF111927)
val SurfaceLight = Color(0xFF1E2936)
val SurfaceElevated = Color(0xFF243447)

// Console/Terminal Colors
val TerminalBackground = Color(0xFF0A0E1A)
val TerminalCyan = Color(0xFF00FFFF)
val TerminalGreen = Color(0xFF00FF66)
val TerminalText = Color(0xFFE0E7FF)

// Status Colors
val SuccessGreen = Color(0xFF00FF88)
val ErrorRed = Color(0xFFFF4757)
val WarningAmber = Color(0xFFFFAA00)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB8C5D6)
val TextTertiary = Color(0xFF8A9BAE)
val TextOnCyan = Color(0xFF001F2B)
