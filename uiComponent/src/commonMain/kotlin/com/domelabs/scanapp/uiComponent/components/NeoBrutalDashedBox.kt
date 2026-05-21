package com.domelabs.scanapp.uiComponent.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.domelabs.scanapp.uiComponent.theme.NeoBlack
import com.domelabs.scanapp.uiComponent.theme.NeoBrutalism
import com.domelabs.scanapp.uiComponent.theme.NeoGrayPlaceholder

@Composable
fun NeoBrutalDashedBox(
    label: String,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    borderColor: Color = NeoBlack,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = Stroke(
                width = NeoBrutalism.BorderWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)),
            )
            drawRoundRect(
                color = borderColor,
                style = stroke,
                cornerRadius = CornerRadius(NeoBrutalism.CornerRadius.toPx()),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = NeoGrayPlaceholder,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
