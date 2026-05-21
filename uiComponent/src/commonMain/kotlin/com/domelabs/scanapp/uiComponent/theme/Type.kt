package com.domelabs.scanapp.uiComponent.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import scan_app.uicomponent.generated.resources.Redhat_SemiBold
import scan_app.uicomponent.generated.resources.Res

@Composable
fun redHatFont() = FontFamily(
    Font(resource = Res.font.Redhat_SemiBold, weight = FontWeight.Normal)
)

val baseline = Typography()

@get:Composable
val ScanAppTypography
    get() = Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = redHatFont()),
        displayMedium = baseline.displayMedium.copy(fontFamily = redHatFont()),
        displaySmall = baseline.displaySmall.copy(fontFamily = redHatFont()),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = redHatFont()),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = redHatFont()),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = redHatFont()),
        titleLarge = baseline.titleLarge.copy(fontFamily = redHatFont()),
        titleMedium = baseline.titleMedium.copy(fontFamily = redHatFont()),
        titleSmall = baseline.titleSmall.copy(fontFamily = redHatFont()),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = redHatFont()),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = redHatFont()),
        bodySmall = baseline.bodySmall.copy(fontFamily = redHatFont()),
        labelLarge = baseline.labelLarge.copy(fontFamily = redHatFont()),
        labelMedium = baseline.labelMedium.copy(fontFamily = redHatFont()),
        labelSmall = baseline.labelSmall.copy(fontFamily = redHatFont()),
    )
