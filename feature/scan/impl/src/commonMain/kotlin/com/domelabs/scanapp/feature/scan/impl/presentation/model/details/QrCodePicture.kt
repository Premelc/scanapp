package com.domelabs.scanapp.feature.scan.impl.presentation.model.details

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import qrgenerator.qrkitpainter.rememberQrKitPainter

@Composable
fun QrCodePicture(
    code: String,
    modifier: Modifier = Modifier,
) {
    val painter = rememberQrKitPainter(code)
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = null
    )
}