package com.domelabs.scanapp.feature.collections.impl.presentation.picker

import androidx.compose.ui.graphics.Color
import com.domelabs.scanapp.uiComponent.theme.PastelBlue
import com.domelabs.scanapp.uiComponent.theme.PastelGreen
import com.domelabs.scanapp.uiComponent.theme.PastelMint
import com.domelabs.scanapp.uiComponent.theme.PastelOlive
import com.domelabs.scanapp.uiComponent.theme.PastelOrange
import com.domelabs.scanapp.uiComponent.theme.PastelSalmon
import com.domelabs.scanapp.uiComponent.theme.PastelTan

object CollectionPickerColors {
    /** Hex strings (lowercased `#rrggbb`) that match the [com.domelabs.scanapp.uiComponent.theme.ScanAppPastels] palette. */
    val palette: List<String> = listOf(
        PastelOrange,
        PastelGreen,
        PastelSalmon,
        PastelOlive,
        PastelTan,
        PastelBlue,
        PastelMint,
    ).map { it.toHex() }
}

fun Color.toHex(): String {
    val argb = value.toLong().shr(32).toInt()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    fun Int.h() = toString(16).padStart(2, '0')
    return "#${r.h()}${g.h()}${b.h()}"
}

fun parseHexColor(hex: String): Color {
    val cleaned = hex.removePrefix("#")
    if (cleaned.length != 6) return Color.Gray
    return try {
        val r = cleaned.substring(0, 2).toInt(16)
        val g = cleaned.substring(2, 4).toInt(16)
        val b = cleaned.substring(4, 6).toInt(16)
        Color(red = r, green = g, blue = b)
    } catch (_: NumberFormatException) {
        Color.Gray
    }
}
